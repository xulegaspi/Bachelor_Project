package lnu.wssrobot.Fondmarknaden;

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

        String website = "http://fondmarknaden.se/Fonder/Sok.aspx";
        String tableId = "ctl00_mastercontent_gvFundList";
        String nextBtnPath = "/html/body/form/div[3]/div[5]/div[2]/div[1]/div/div/span[1]/div/table/tbody/tr[33]/td/div/div[2]/ul/li[7]/a";
        int numRows = 30;

        Spec myCrawler = new Fondmarknaden_fr(website, tableId, nextBtnPath, numRows);

        FundCrawlerFramework producer = new FundCrawlerFramework(producer2consumer, "trying", myCrawler);
        producer.start();

    }

}