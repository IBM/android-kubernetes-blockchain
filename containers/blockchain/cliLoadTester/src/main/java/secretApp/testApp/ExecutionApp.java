package secretApp.testApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ExecutionApp {
	private static String executionURL = "http://localhost:3000/api/execute";
	private static String dbName = "testResults";
	private static int count = 0;
	private static int steps = 100;
	private static int fixOperations = 10;
	private static int totalUsers = 1500;
	private static int queuedOperations = 2000;

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			while (true) {
				MongoClient mongo = getConnection();
				if (mongo == null) {
					System.exit(0);
				}
				Set<DBObject> dbobj = getUserObjects("results", mongo);
				closeConnection(mongo);
				if (dbobj.size() > queuedOperations) {
					System.out.println("Wait for 3 min to finsh execution of queued request");
					Thread.currentThread().sleep(180000);
				} else {
					mongo = getConnection();
					if (mongo == null) {
						System.exit(0);
					}
					dbobj = getUserObjects("users", mongo);
					closeConnection(mongo);
					if (dbobj.size() > 10) {
						performQueryOpertion(fixOperations);
						System.out.println("Total Operations queue : " + count);
						performInvokeOpertion(fixOperations, String.valueOf(steps));
						System.out.println("Total Operations queue : " + count);
						performQueryOpertion(fixOperations);
						System.out.println("Total Operations queue : " + count);
						steps += 10;
						if (dbobj.size() < totalUsers) {
							System.out.println("Enrolling users");
							enrollUsers(fixOperations);
						}
					} else {
						System.out.println("Enrolling users");
						enrollUsers(fixOperations);
						System.out.println("Total Operations queue : " + count);
						Thread.currentThread().sleep(10000);
					}

				}

			}

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}

	private static MongoClient getConnection() {
		int attempt = 0;
		MongoClient mongo = null;
		while (mongo == null && attempt <= 10) {
			attempt++;
			try {
				mongo = new MongoClient("localhost", 27017);
				break;
			} catch (Exception e) {
				mongo = null;
			}
		}
		return mongo;
	}

	private static void closeConnection(MongoClient mongo) {
		mongo.close();
	}

	private static void executeRequest(String data, MongoClient mongo) {
		String body;
		try {
			body = Task.post(executionURL, data);
			JsonObject jsonObj = new JsonParser().parse(body).getAsJsonObject();
			if (jsonObj.get("status").toString().equals("\"success\"")) {
				DB database = mongo.getDB(dbName);
				DBCollection collection = Task.getDBCollection(database, "results");
				List<DBObject> list = new ArrayList<>();
				BasicDBObject dataObject = new BasicDBObject();
				dataObject.append("resultId", jsonObj.get("resultId").getAsString());
				dataObject.append("query", data);
				list.add(dataObject);
				collection.insert(list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Set<DBObject> getUserObjects(String collectionName, MongoClient mongo) {
		Set<DBObject> users = new HashSet<>();
		try {
			DB database = mongo.getDB(dbName);
			DBCollection collection = Task.getDBCollection(database, collectionName);
			DBCursor cursor = collection.find();
			while (cursor.hasNext()) {
				users.add(cursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	private static void performQueryOpertion(int number) {
		MongoClient mongo = getConnection();
		if (mongo == null) {
			System.exit(0);
		}
		Set<DBObject> users = getUserObjects("users", mongo);
		int temp = 0;
		for (DBObject dbObject : users) {
			count++;
			temp++;
			String userId = dbObject.get("user").toString();
			String query = "type=query&queue=user_queue&params={\"userId\":\"" + userId
					+ "\" , \"fcn\":\"getState\" ,\"args\":[\"" + userId + "\"]}";

			executeRequest(query, mongo);
			if (temp >= number) {
				break;
			}
		}
		closeConnection(mongo);
	}

	private static void performInvokeOpertion(int number, String steps) {
		MongoClient mongo = getConnection();
		if (mongo == null) {
			System.exit(0);
		}
		Set<DBObject> users = getUserObjects("users", mongo);
		int temp = 0;
		for (DBObject dbObject : users) {
			count++;
			temp++;
			String userId = dbObject.get("user").toString();
			String query = "type=invoke&queue=user_queue&params={ \"userId\":\"" + userId
					+ "\",\"fcn\":\"generateFitcoins\",\"args\":[\"" + userId + "\",\"" + steps + "\"]}";
			// executorService.execute(new ExecutionTask(query, executionURL,
			// dbName));
			executeRequest(query, mongo);
			if (temp >= number) {
				break;
			}
		}
		closeConnection(mongo);
	}

	private static void enrollUsers(int number) {
		MongoClient mongo = getConnection();
		if (mongo == null) {
			System.exit(0);
		}
		for (int i = 0; i < number; i++) {
			count++;
			// executorService.execute(new
			// ExecutionTask("type=enroll&queue=user_queue&params={}",
			// executionURL, dbName));
			executeRequest("type=enroll&queue=user_queue&params={}", mongo);
		}
		closeConnection(mongo);
	}
}
