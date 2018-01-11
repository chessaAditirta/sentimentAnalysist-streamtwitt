package com.example;

import com.connectMongo.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
public class ShowMongod {
	public static void main(String[] args) {
		ConnectMongo kon = new ConnectMongo();
		kon.konekMongo();
		Mongo mongo = new Mongo("localhost",27017);
		DB db = mongo.getDB("twitter");
		
		DBCollection collection = db.getCollection("tweet");
		
		BasicDBObject whereQuery = new BasicDBObject();
	    whereQuery.put("title", "MongoDB");
		BasicDBObject row = new BasicDBObject();
		row.put("text", true);
		DBCursor cursor = collection.find(whereQuery,row);
		
		while (cursor.hasNext()) {
			System.out.println(cursor.next().get("text"));
		}
	}
}