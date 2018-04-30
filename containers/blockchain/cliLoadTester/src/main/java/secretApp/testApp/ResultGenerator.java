package secretApp.testApp;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ResultGenerator {
	private static String resultURL = "http://localhost:3000/api/results/";
	private static String dbName = "testResults";

	public static void main(String[] args) throws UnknownHostException {
		System.out.println("scheduling task to be executed every 2 seconds with an initial delay of 0 seconds");
		MongoClient mongo;
		mongo = new MongoClient("localhost", 27017);
		while (true) {
			try {

				DB database = mongo.getDB(dbName);
				DBCollection collection = Task.getDBCollection(database, "results");
				DBCursor cursor = collection.find();
				List<DBObject> removelist = new ArrayList<>();
				while (cursor.hasNext()) {
					DBObject object = cursor.next();
					String resultId = object.get("resultId").toString();
					String resultsData = Task.get(resultURL + resultId);
					JsonObject jsonObj = new JsonParser().parse(resultsData).getAsJsonObject();
					if (jsonObj.get("status").getAsString().equals("done")) {
						String resultString = jsonObj.get("result").toString();
						resultString = resultString.substring(1, resultString.length() - 1);
						resultString = resultString.replaceAll("\\\\\"", "\"");
						resultString = resultString.replaceAll("\\\\\"", "\"");
						// System.out.println(resultString);
						JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultString);
						String queryResultStatus = jsonObject.get("message").toString();
						DBCollection resultCollection;
						String query = object.get("query").toString();
						if (query.contains("enroll")) {
							String db = "users";
							BasicDBObject dataObject = new BasicDBObject();
							if (queryResultStatus.contains("success")) {
								resultCollection = Task.getDBCollection(database, db);
								JSONObject userObj = (JSONObject) jsonObject.get("result");
								if (userObj != null) {
									dataObject.append("user", userObj.get("user"));
								}

							} else {
								resultCollection = Task.getDBCollection(database, "failed" + db);
								dataObject.append("queue",
										query.contains("user_queue") ? "user_queue" : "seller_queue");
								dataObject.append("error", jsonObject.get("error"));
							}
							resultCollection.insert(dataObject);
						} else {
							String db = "_query";
							if (query.contains("invoke")) {
								db = "_invoke";
							}
							if (queryResultStatus.contains("success")) {
								resultCollection = Task.getDBCollection(database, "success" + db);
								BasicDBObject searchQuery = new BasicDBObject().append("id", "count");
								BasicDBObject modifiedObject = new BasicDBObject();
								modifiedObject.append("$inc", new BasicDBObject().append("value", 1));
								// parentObject.removeField("value");
								resultCollection.update(searchQuery, modifiedObject);

							} else {
								resultCollection = Task.getDBCollection(database, "failed" + db);
								BasicDBObject dataObject = new BasicDBObject();
								dataObject.append("query", query);
								dataObject.append("error", jsonObject.get("error"));
								resultCollection.insert(dataObject);
							}
						}
						removelist.add(object);
					}

				}
				if (removelist.size() > 0) {
					for (DBObject dbObject : removelist) {
						collection.remove(dbObject);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
