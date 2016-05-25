package lnu.wssrobot;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Xurxo on 27/04/2016.
 */
public class FundCrawlerFramework extends Thread implements FundCrawler {

    private Spec spec;
    private int id_fund;
    private int iteration;
    private HashMap<String, Integer> allFundsMap = new HashMap<>();
    private ArrayList<Fund> allFunds = new ArrayList<Fund>();
    private final BlockingQueue<JsonObject> jsonQueue;  // Our output channel
    private HashMap<Integer, Fund> id2fund = new HashMap<Integer, Fund>();

    private String website;
    private String tableId;
    private String nextBtnPath;
    private int numRows;

    private int currencyCell;
    private int titleCell;
    private int urlCell;
    private int datumCell;
    private int priceCell;

    public FundCrawlerFramework(BlockingQueue<JsonObject> queue, String nameThread, Spec spec) {
        super(nameThread);
        this.spec = spec;
        this.website = spec.website;
        this.tableId = spec.tableId;
        this.nextBtnPath = spec.nextBtnPath;
        this.numRows = spec.numRows;

        this.currencyCell = spec.currencyCell;
        this.titleCell = spec.titleCell;
        this.urlCell = spec.urlCell;
        this.datumCell = spec.datumCell;
        this.priceCell = spec.priceCell;

        id_fund = 0;
        iteration = 0;
        System.out.println("Enter "+this.getClass().getName());
        jsonQueue = queue;
    }

    @Override
    public void run() {
        while (true) {

            // Open the WebClient
            WebClient webClient = new WebClient();
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.setIncorrectnessListener((arg0, arg1) -> {
                // TODO Auto-generated method stub

            });
            webClient.setCssErrorHandler(new ErrorHandler() {

                @Override
                public void warning(CSSParseException exception) throws CSSException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void fatalError(CSSParseException exception) throws CSSException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void error(CSSParseException exception) throws CSSException {
                    // TODO Auto-generated method stub

                }
            });
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            id_fund = 0;
            iteration++;
            System.out.println("Iteration n: " + iteration);
            allFunds = parseAllFunds(webClient);

            // Close the WebClient
            webClient.close();

        }
    }

    @Override
    public Fund parseFund(HtmlTableRow row) {
        id_fund++;

        // Set timestamp
        long timeStamp = System.currentTimeMillis();

        String title = spec.getTitle(row);
        String url = spec.getUrl(row);
        String currency = spec.getCurrency(row);
        String datum = spec.getDatum(row);
        Double price = spec.getPrice(row);

        if(title == null || currency == null || price == null) {
            return null;
        }
        Fund fund =  new Fund(id_fund, title, url, price, currency, timeStamp, datum);

        return fund;
    }

    @Override
    public ArrayList<Fund> parseAllFunds(WebClient webClient) {

        HtmlPage page = null;
        ArrayList<Fund> myAllFunds = allFunds;
        ArrayList<Fund> myPageFunds = new ArrayList<Fund>();

        // Initialize the variables in order to safe memory using the GC
        HtmlAnchor next = null;
        HtmlTable table = null;
        Boolean stop = false;
        List<HtmlTableRow> rows = null;
        Fund fund = null;

        page = getPage(webClient, website);

        int page_num = 1;

        while(!stop) {
            myPageFunds.clear();
            System.gc();

            table = getTable(page);
            rows = getRows(table);

            if(rows.size() < numRows) {
                stop = true;
            }

            for(HtmlTableRow row : rows) {

                fund = parseFund(row);

                if(fund != null) {
                    boolean update = false;
                    long timeStamp = 0;

                    if (findFund(fund, myAllFunds) != null) {

                        Fund f = findFund(fund, myAllFunds);

                        if (f.getCurrentPrice() != fund.getCurrentPrice()) {
                            System.out.print("UPDATE: Price -> " + f.getCurrentPrice() + " || " + fund.getCurrentPrice());
                            f.setCurrentPrice(fund.getCurrentPrice());
                            System.out.println(" || " + f.toString());
                            update = true;
                        }

                        if (f.getDatum().equals(fund.getDatum())) {
                            System.out.print("UPDATE: Datum -> " + f.getDatum() + " || " + fund.getDatum());
                            f.setDatum(fund.getDatum());
                            System.out.println(" || " + f.toString());
                            update = true;
                        }

                        // Update discovery time
                        timeStamp = System.currentTimeMillis();
                        f.setDiscoveryTime(timeStamp);

                        if (update) {
                            myPageFunds.add(f);
                        }

                    } else {
                        //System.out.println("Adding: " + fund.toString());
                        System.out.print("A ");
                        if (id_fund % 20 == 0) {
                            System.out.print("\n");
                        }
                        myAllFunds.add(fund);
                        myPageFunds.add(fund);
                    }
                }

            }

            // We send the new found funds to the Json Consumer
            for (Fund f : myPageFunds) {
                int id = f.getID();
                id2fund.put(id, f);
                Util.addToQueue(jsonQueue, f);
            }

            page_num++;

            HtmlAnchor nextBtn = getNextButton(page);
            page = getNextPage(nextBtn);
            if(page == null) {
                stop = true;
            }

        }

        return myAllFunds;
    }

    @Override
    public HtmlPage getPage(WebClient webClient, String website) {
        try {
            HtmlPage page = webClient.getPage(website);
            // Add necessary steps to get the page
            page = spec.extraSteps(page);

            try {
                sleep(1000);
            } catch (Exception e) {
                System.out.println("Sleep 1 failed");
            }
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HtmlAnchor getNextButton(HtmlPage currentPage) {
        try {
            // Select the button "next"
            HtmlAnchor next = (HtmlAnchor) currentPage.getByXPath(nextBtnPath).get(0);
            return next;
        } catch (Exception e) {
            // Stop if the button can't be found
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HtmlPage getNextPage(HtmlAnchor nextButton) {
        // Click to get the next page
        try {
            if(nextButton == null) {
                System.out.println("Couldn't find next button.");
                return null;
            }

            HtmlPage page = nextButton.click();
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(page == null) {
                System.out.println("Couldn't load the next page, trying again...");
                page = nextButton.click();
            }
            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HtmlTable getTable(HtmlPage page) {
        Boolean correct = false;
        while(!correct) {
            try {
                HtmlTable table = page.getHtmlElementById(tableId);
                correct = true;
                sleep(1000);
                return table;
            } catch (Exception e) {
                try {
                    System.out.println("Fetching table failed, going to sleep and try again...");
                    sleep(1000);
                } catch (Exception e2) {
                    System.out.println("Sleep failed");
                }
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<HtmlTableRow> getRows(HtmlTable table) {
        List<HtmlTableRow> rows = null;
        for (HtmlTableBody body : table.getBodies()) {
            rows = body.getRows();
        }
        return rows;
    }

    @Override
    public Fund findFund(Fund fund, ArrayList<Fund> myFunds) {
        if (fund != null) {
            String name_1 = fund.getFundName();
            String curr_1 = fund.getCurrencyCode();

            // Search for the fund in the cache
            for (Fund f : myFunds) {
                String name_2 = f.getFundName();
                String curr_2 = f.getCurrencyCode();

                // Check if the name and the currency are the same (to uniquely identify the fund)
                if (name_1.equals(name_2) && curr_1.equals(curr_2)) {
                    return f;
                }

            }
        }

        return null;
    }

}
