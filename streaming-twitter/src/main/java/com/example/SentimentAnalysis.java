package com.example;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import twitter4j.TwitterException;

class graphic {
	public void gra(int p, int n) {
		DefaultPieDataset dpd = new DefaultPieDataset();
		int to = n + p;
		double ton = (n*100)/to;
		double top = (p*100)/to;
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		double hton = Double.valueOf(twoDForm.format(ton));
		double htop = Double.valueOf(twoDForm.format(top));
	     dpd.setValue("NEGATIF", hton);
	     dpd.setValue("POSITIF", htop);

	     JFreeChart freeChart = ChartFactory.createPieChart("Dari "+to+" Tweets tentang Joko Widodo",
	    		 dpd,true,true, false); 
	     ChartFrame cf = new ChartFrame("Data Tanggapan Pengguna Twitter ",freeChart);
	     cf.setSize(1000,800);
	     cf.setVisible(true);
	     cf.setLocationRelativeTo(null);
	}   
}

public class SentimentAnalysis {
	DoccatModel model;
    static int positive = 0;
    static int negative = 0;
    
    public static void main(String[] args) throws IOException, TwitterException {
        String line = "";
        SentimentAnalysis twitterCategorizer = new SentimentAnalysis();
        twitterCategorizer.trainModel();

        Mongo mongo = new Mongo("localhost",27017);
		DB db = mongo.getDB("twitter");
		
		DBCollection collection = db.getCollection("tweet");
		
		BasicDBObject whereQuery = new BasicDBObject();
	    whereQuery.put("title", "MongoDB");
		BasicDBObject row = new BasicDBObject();
		row.put("text", 1);
		  DBCursor cursor = collection.find(whereQuery,row);
		  Iterator<DBObject> field=cursor.iterator();
		  while (field.hasNext()) {
			  DBObject obj = (DBObject) field.next();
//			  System.out.println(""+obj.get("text"));			  
		        int result1 = 0;
		            result1 = twitterCategorizer.classifyNewTweet(""+obj.get("text"));
		            if (result1 == 1) {
		                positive++;
		            } else {
		                negative++;
		            }

//		        BufferedWriter bw = new BufferedWriter(new FileWriter("/home/aqor/eclipse-workspace/stream_twitter/input/results.csv"));
//		        bw.write("Positive Tweets," + positive);
//		        bw.newLine();
//		        bw.write("Negative Tweets," + negative);
//		        bw.close();
			  
		  }
		  
		  graphic g = new graphic();
		  g.gra(positive, negative);
    }

    public void trainModel() {
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream("/root/eclipse-workspace/streaming-twitter/input/tweets");
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);
            // Specifies the minimum number of times a feature must be seen
            int cutoff = 2;
            int trainingIterations = 30;
            model = DocumentCategorizerME.train("en", sampleStream, cutoff,
                    trainingIterations);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataIn != null) {
                try {
                    dataIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int classifyNewTweet(String tweet) throws IOException {
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
        double[] outcomes = myCategorizer.categorize(tweet);
        String category = myCategorizer.getBestCategory(outcomes);

        System.out.print("-----------------------------------------------------\nTWEET :" + tweet + " ===> ");
        if (category.equalsIgnoreCase("1")) {
            System.out.println(" POSITIVE ");
            return 1;
        } else {
            System.out.println(" NEGATIVE ");
            return 0;
        }
    }
}