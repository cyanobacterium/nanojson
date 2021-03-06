package com.grack.nanojson.perf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.caliper.Benchmark;
import com.google.caliper.runner.CaliperMain;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParser.JsonParserContext;
import com.grack.nanojson.JsonParserException;

/**
 * Caliper test to compare parsing of strings vs. UTF-8 streams of GSON, Jackson
 * and nanojson.
 */
public class Perf {
	// CHECKSTYLE_OFF: JavadocMethod
	/**
	 * The parsing benchmark.
	 */
	public static class Parse {
		private static final int BUFFER_SIZE = 1024 * 1024;
		private String string;
		private URL url;
		private ObjectMapper mapper = new ObjectMapper();

		/**
		 * Quick-and-dirty UTF8 reader.
		 */
		private static String readAsUtf8(InputStream input) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[BUFFER_SIZE];
			while (true) {
				int r = input.read(b);
				if (r <= 0)
					break;
				out.write(b, 0, r);
			}
			Charset utf8 = Charset.forName("UTF8");
			String s = new String(out.toByteArray(), utf8);
			return s;
		}

		public Parse() throws IOException {
			url = Perf.class.getClassLoader().getResource("sample.json");
			InputStream stm = url.openStream();
			string = readAsUtf8(stm);
		}

		public void timeNanojsonLazyNumberString(int reps)
				throws JsonParserException {
			JsonParserContext<JsonObject> parser = JsonParser.object()
					.withLazyNumbers();
			for (int i = 0; i < reps; i++) {
				parser.from(string);
			}
		}

		public void timeNanojsonLazyNumberStream(int reps)
				throws JsonParserException, IOException {
			JsonParserContext<JsonObject> parser = JsonParser.object()
					.withLazyNumbers();
			for (int i = 0; i < reps; i++) {
				InputStream stm = url.openStream();
				parser.from(stm);
				stm.close();
			}
		}

		public void timeNanojsonString(int reps) throws JsonParserException {
			JsonParserContext<JsonObject> parser = JsonParser.object();
			for (int i = 0; i < reps; i++) {
				parser.from(string);
			}
		}

		public void timeNanojsonStream(int reps) throws JsonParserException,
				IOException {
			JsonParserContext<JsonObject> parser = JsonParser.object();
			for (int i = 0; i < reps; i++) {
				InputStream stm = url.openStream();
				parser.from(stm);
				stm.close();
			}
		}

		public void timeJacksonString(int reps) throws JsonParseException,
				JsonMappingException, IOException {
			for (int i = 0; i < reps; i++) {
				mapper.readValue(string, JsonNode.class);
			}
		}

		public void timeJacksonStream(int reps) throws JsonParseException,
				JsonMappingException, IOException {
			for (int i = 0; i < reps; i++) {
				InputStream stm = url.openStream();
				mapper.readValue(stm, JsonNode.class);
				stm.close();
			}
		}

		public void timeGsonString(int reps) {
			com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
			for (int i = 0; i < reps; i++) {
				parser.parse(string);
			}
		}

		public void timeGsonStream(int reps) throws IOException {
			com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
			for (int i = 0; i < reps; i++) {
				InputStream stm = url.openStream();
				parser.parse(new InputStreamReader(stm));
				stm.close();
			}
		}

		public static void main(String[] args) {
			CaliperMain.main(Perf.Parse.class, args);
		}
	}
}
