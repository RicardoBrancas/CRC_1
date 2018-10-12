package pt.tecnico.crc.wikilink;

import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.Stats;

import java.io.IOException;

public class App {

	public static void main(String[] args) throws IOException {

		String basename_file = App.class.getClassLoader().getResource("wikipedia_pt").getFile();

		if (basename_file == null) {
			System.err.println("Files not found in classpath!");
			System.exit(-1);
		}

		ImmutableGraph graph = ImmutableGraph.load(basename_file);

		ProgressLogger pl = new ProgressLogger();

		Stats.run(graph, null, null, "output", pl);
	}

}
