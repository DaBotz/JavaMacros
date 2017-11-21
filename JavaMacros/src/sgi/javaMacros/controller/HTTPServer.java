package sgi.javaMacros.controller;

import static sgi.javaMacros.controller.LuaUpdater.crlf;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPServer implements Runnable {

	private static final String UPDATES_ = "updates ";

	public static final String ALREADY_ACTIVE = "AlreadyActive";

	private JavaMacrosController controller;
	protected int httpServerPort;
	private ArrayList<String> updates = new ArrayList<>();

	public synchronized void addUpdates(ArrayList<String> newUpdates) {
		updates.addAll(newUpdates);
	}

	public HTTPServer(JavaMacrosController javaMacrosController) {
		this.controller = javaMacrosController;
	}

	@Override
	public void run() {
		try {

			httpServerPort = controller.getHttpServerPort();
			HttpServer server = HttpServer.create(new InetSocketAddress(httpServerPort), 0);
			System.out.println("server started at " + httpServerPort);
			DeviceHandler egh = new DeviceHandler();

			server.createContext("/", egh);
			server.createContext("/kbd", egh);
			server.createContext("/dev", egh);
			server.createContext("/init", new InitializationHandler());
			server.setExecutor(null);
			server.start();

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public class RootHandler implements HttpHandler {

		@Override

		public void handle(HttpExchange he) throws IOException {
			String response = "<h1>Server start success " + "if you see this message</h1>" + "<h1>Port: "
					+ httpServerPort + "</h1>";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	public class TesttHandler implements HttpHandler {

		@Override

		public void handle(HttpExchange he) throws IOException {
			String response = ALREADY_ACTIVE;
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	public class DeviceHandler implements HttpHandler {
		@Override

		public void handle(HttpExchange he) throws IOException {
			// parse request
			Map<String, Object> parameters = new HashMap<>();
			URI requestURI = he.getRequestURI();
			String query = requestURI.getRawQuery();
			parseQuery(query, parameters);

			String response = controller.processDeviceEvent(//
					parameters.get("source"), //
					parameters.get("k"), //
					parameters.get("d"), //
					parameters.get("t"));

//			for (String key : parameters.keySet())
//				response += crlf + "  " + key + " = " + parameters.get(key) + " ";

			if (response.startsWith("OK") && updates.size() > 0) {
				response += crlf + UPDATES_ + stringUpdates();
			}
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());

			os.close();

			System.out.println(parameters);
		}
	}

	public class InitializationHandler implements HttpHandler {
		@Override

		public void handle(HttpExchange he) throws IOException {
			addUpdates(controller.getLuaInitializationCode());
			String response = "";
			if (updates.size() > 0) {
				response += UPDATES_ + stringUpdates();
			}
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();

		}
	}


	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}

	public void addUpdate(String update) {
		updates.add(update);

	}

	protected String stringUpdates() {
		String updateString = "";

		for (String string : updates) {
			updateString += string + crlf;
		}

		updates.clear();
		return updateString;
	}

}
