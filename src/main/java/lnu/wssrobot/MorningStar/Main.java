package lnu.wssrobot.MorningStar;

import lnu.wssrobot.FakeJsonConsumer;
import lnu.wssrobot.FundCrawlerFramework;
import lnu.wssrobot.JsonObject;
import lnu.wssrobot.Spec;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Xurxo on 28/04/2016.
 */
public class Main {

    public static void main(String[] args) {

        BlockingQueue<JsonObject> producer2consumer = new LinkedBlockingQueue<JsonObject>();

        FakeJsonConsumer consumer = new FakeJsonConsumer(producer2consumer);
        consumer.start();

        String website = "http://www.morningstar.se/Funds/Quickrank.aspx?cb=on";
        String tableId = "ctl01_cphContent_Quickrank1_ctl00";
        String nextBtnPath = "/html/body/form/div[4]/div[4]/div[2]/div[9]/table/tfoot/tr/td/a[25]";
        int numRows = 20;

        Spec myCrawler = new MorningStar_fr(website, tableId, nextBtnPath, numRows);

        FundCrawlerFramework producer = new FundCrawlerFramework(producer2consumer, "trying", myCrawler);
        producer.start();

    }

}