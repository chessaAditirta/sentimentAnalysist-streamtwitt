package com.example;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import twitter4j.FilterQuery;
import twitter4j.JSONObject;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StreamingTwitter {
	public static void main(String[] args) {
		final String consumerKey = "lSCIyi7zfutLMwFH0tukMhZhj";
		final String consumerSecret = "mCrfq9rliQXe7ha7Z7b5uVQG7xa2FS8ZnbJq2wd4A6Y2jT4bn4";
		final String consumerToken = "939020922446528512-9Ie7PTVvbcfPAKn0rydpBJDkS0ijhrl";
		final String consumerTokenSecret = "1mz8uUzc1Qu5IDcRjBTuKgEejnii1dDjtLlKhl2ZzIznJ";
		
		String[] keywords= {"Ustad Abdul Somad","Deportasi dari Hongkong", "UAS"};
		
		FilterQuery qry = new FilterQuery();
		qry.track(keywords);
		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(consumerKey)
		.setOAuthConsumerSecret(consumerSecret)
		.setOAuthAccessToken(consumerToken)
		.setOAuthAccessTokenSecret(consumerTokenSecret);
		
		TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
		
		StatusListener Listener2 = null;
		twitterStream.addListener(Listener2);
		
		twitterStream.addListener(new StatusListener() {

			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onStatus(Status arg0) {
				// TODO Auto-generated method stub
				JSONObject message = new JSONObject(arg0);
                SendKafka(""+message);
                System.out.println(message);
                
				
			}

			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub
				
			}});
		twitterStream.filter(qry);
		
	}
	public static void SendKafka(String message) {
		 
        String BOOTSTRAP_SERVERS = "localhost:9092";
        String topic = "akakom";
 
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
 
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        try {
            producer.send(new ProducerRecord<String, String>(topic, message));
            producer.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
}

