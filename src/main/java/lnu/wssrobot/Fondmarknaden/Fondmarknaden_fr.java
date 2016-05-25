package lnu.wssrobot.Fondmarknaden;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import lnu.wssrobot.Spec;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Xurxo on 28/04/2016.
 */
public class Fondmarknaden_fr extends Spec {

    private int id_fund = 0;

    public Fondmarknaden_fr(String website, String tableId, String nextBtnPath, int numRows) {
        super(website, tableId, nextBtnPath, numRows);
    }

    @Override
    public String getDatum(HtmlTableRow row) {
        String datum = row.getCell(datumCell).getTextContent();
        return datum;
    }

    @Override
    public String getUrl(HtmlTableRow row) {
        return "";
    }

    @Override
    public String getTitle(HtmlTableRow row) {
        int titleCell = 1;
        try {
            String title = row.getCell(titleCell).getTextContent();
            title = title.replaceAll("\\s", "");
            return title;
        } catch (Exception e) {
            System.out.println("Couldn't get title of row.");
        }
        return null;
    }

    @Override
    public String getCurrency(HtmlTableRow row) {
        int currencyCell = 2;
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            String st = row.getCell(currencyCell).getTextContent();
            st = st.replaceAll("\\s", "");
            String currency = st.substring(st.length()-3, st.length());
            return currency;
        } catch (Exception e) {
            System.out.println("Error while getting the currency & price.");
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double getPrice(HtmlTableRow row) {
        int priceCell = 2;
        double price = 0.0;
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            String st = row.getCell(priceCell).getTextContent();
            st = st.replaceAll("\\s", "");
            Number number = format.parse(st.substring(0, st.length()-3));
            price = number.doubleValue();
            return price;
        } catch (Exception e) {
            System.out.println("Error while getting the currency & price.");
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HtmlPage extraSteps(HtmlPage currentPage) {
        return currentPage;
    }

}
