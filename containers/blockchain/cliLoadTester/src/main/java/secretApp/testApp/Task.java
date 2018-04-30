package secretApp.testApp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public abstract class Task {

	public static String get(String getUrl) throws IOException {
		URL url = new URL(getUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		return read(con.getInputStream());
	}

	public static String post(String postUrl, String data) throws IOException {
		URL url = new URL(postUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");

		con.setDoOutput(true);

		sendData(con, data);

		return read(con.getInputStream());
	}

	public static void sendData(HttpURLConnection con, String data) throws IOException {
		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
		} catch (IOException exception) {
			throw exception;
		} finally {
			closeQuietly(wr);
		}
	}

	public static String read(InputStream is) throws IOException {
		BufferedReader in = null;
		String inputLine;
		StringBuilder body;
		try {
			in = new BufferedReader(new InputStreamReader(is));

			body = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				body.append(inputLine);
			}
			in.close();

			return body.toString();
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			closeQuietly(in);
		}
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ex) {

		}
	}

	public static DBCollection getDBCollection(DB database, String collectionName) {
		if (!database.collectionExists(collectionName)) {
			BasicDBObject basicDBObject = new BasicDBObject();
			database.createCollection(collectionName, basicDBObject);
			if (!(collectionName.equals("users") || collectionName.equals("results"))) {
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.append("id", "count");
				dbObject.append("value", 0);
				database.getCollection(collectionName).insert(dbObject);
			}

			System.out.println("Collection created successfully : " + collectionName);
		}
		return database.getCollection(collectionName);
	}
}
