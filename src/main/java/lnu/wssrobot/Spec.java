package lnu.wssrobot;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * Created by Xurxo on 28/04/2016.
 */
public abstract class Spec {

    public String website;
    public String tableId;
    public String nextBtnPath;
    public int numRows;
    public int id_fund;

    public int currencyCell;
    public int titleCell;
    public int urlCell;
    public int datumCell;
    public int priceCell;

    public Spec(String website, String tableId, String nextBtnPath, int numRows) {
        this.website = website;
        this.tableId = tableId;
        this.nextBtnPath = nextBtnPath;
        this.numRows = numRows;
        this.id_fund = 0;
    }

    public abstract String getDatum(HtmlTableRow row);

    public abstract String getUrl(HtmlTableRow row);

    public abstract String getTitle(HtmlTableRow row);

    public abstract String getCurrency(HtmlTableRow row);

    public abstract Double getPrice(HtmlTableRow row);

    public abstract HtmlPage extraSteps(HtmlPage currentPage);

}
