package lnu.wssrobot;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xurxo on 27/04/2016.
 */
public interface FundCrawler extends Runnable {

    void run() throws FailingHttpStatusCodeException;

    Fund parseFund(HtmlTableRow row);

    ArrayList<Fund> parseAllFunds(WebClient webClient);

    HtmlPage getPage(WebClient webClient, String website);

    HtmlAnchor getNextButton(HtmlPage currentPage);

    HtmlPage getNextPage(HtmlAnchor nextButton);

    HtmlTable getTable(HtmlPage page);

    List<HtmlTableRow> getRows(HtmlTable table);

    Fund findFund(Fund fund, ArrayList<Fund> myFunds);

}
