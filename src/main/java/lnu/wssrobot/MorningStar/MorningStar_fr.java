package lnu.wssrobot.MorningStar;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import lnu.wssrobot.Spec;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Xurxo on 28/04/2016.
 */
public class MorningStar_fr extends Spec {
    public MorningStar_fr(String website, String tableId, String nextBtnPath, int numRows) {
        super(website, tableId, nextBtnPath, numRows);
    }

    @Override
    public String getDatum(HtmlTableRow row) {
        String datum = row.getCell(11).getTextContent();
        return datum;
    }

    @Override
    public String getUrl(HtmlTableRow row) {
        String url = "";
        return url;
    }

    @Override
    public String getTitle(HtmlTableRow row) {
        String title = row.getCell(2).getTextContent();
        return title;
    }

    @Override
    public String getCurrency(HtmlTableRow row) {
        String currency = row.getCell(4).getTextContent();
        return currency;
    }

    @Override
    public Double getPrice(HtmlTableRow row) {
        double price = 0.0;
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            String st = row.getCell(3).getTextContent();
            st = st.replaceAll("\\s", "");
            Number number = format.parse(st);
            price = number.doubleValue();
            return price;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }

    @Override
    public HtmlPage extraSteps(HtmlPage currentPage) {
        String tabPath = "/html/body/form/div[4]/div[4]/div[2]/div[7]/div/div/div/ul/li[2]/a";
        try {
            HtmlAnchor tab = (HtmlAnchor) currentPage.getByXPath(tabPath).get(0);
            HtmlPage page = tab.click();
            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
