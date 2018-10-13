/*		 
 * Copyright (C) 2011-2013 Sebastiano Vigna 
 * Copyright (C) 2013 A P Francisco
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */

import it.unimi.dsi.Util;
import it.unimi.dsi.fastutil.ints.AbstractIntComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.NodeIterator;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

/**
 * Computes the connected components of a graph.
 * 
 * <p>The {@link #compute(ImmutableGraph, int, ProgressLogger)} method of this class will return an
 * instance that contains the data computed by visiting the graph (loaded offline).
 * 
 * <p>After getting an instance, it is possible to run the {@link #computeSizes()} and
 * {@link #sortBySize(int[])} methods to obtain further information. This scheme has been devised to
 * exploit the available memory as much as possible&mdash;after the components have been computed,
 * the returned instance keeps no track of the graph, and the related memory can be freed by the
 * garbage collector.
 */

public class ConnectedComponents {
	private static final Logger LOGGER = LoggerFactory.getLogger( ConnectedComponents.class );

	/** The number of connected components. */
	public final int numberOfComponents;

	/** The component of each node. */
	public final int component[];

	protected ConnectedComponents( final int numberOfComponents, final int[] component ) {
		this.numberOfComponents = numberOfComponents;
		this.component = component;
	}

	/**
	 * Computes the diameter of a symmetric graph.
	 * 
	 * @param symGraph a symmetric graph.
	 * @param pl a progress logger, or <code>null</code>.
	 * @return an instance of this class containing the computed components.
	 */
	public static ConnectedComponents compute( final ImmutableGraph g, final ProgressLogger pl ) {

		DisjointSet set = new DisjointSet(g.numNodes());
		LOGGER.info("Computing CCs...");
		pl.start("Processing " + g.numNodes() + " nodes...");
		pl.expectedUpdates = g.numNodes();
		pl.itemsName = "nodes";
		
		NodeIterator nIter = g.nodeIterator();
		while (nIter.hasNext()) {
			int u =	nIter.nextInt();
			LazyIntIterator eIter = nIter.successors();
			
			int v = 0;
			while ((v = eIter.nextInt()) != -1) {
				set.unionSet(u, v);
			}
 		pl.update();
		}
		pl.done();

		int ccid = 0;
		final int[] component = new int[ g.numNodes() ];
		for ( int i = component.length; i-- != 0; )
			component[ i ] = -1;
		for ( int i = component.length; i-- != 0; ) {
			if (component[ set.findSet(i) ] == -1)
				component[ set.findSet(i) ] = ccid++;
			component[ i ] = component[ set.findSet(i) ];
		}

		return new ConnectedComponents( ccid, component );
	}

	/**
	 * Returns the size array for this set of connected components.
	 * 
	 * @return the size array for this set of connected components.
	 */
	public int[] computeSizes() {
		final int[] size = new int[ numberOfComponents ];
		for ( int i = component.length; i-- != 0; )
			size[ component[ i ] ]++;
		return size;
	}

	/**
	 * Renumbers by decreasing size the components of this set.
	 * 
	 * <p>After a call to this method, both the internal status of this class and the argument array
	 * are permuted so that the sizes of connected components are decreasing in the component index.
	 * 
	 * @param size the components sizes, as returned by {@link #computeSizes()}.
	 */
	public void sortBySize( final int[] size ) {
		final int[] perm = Util.identity( size.length );
		IntArrays.quickSort( perm, 0, perm.length, new AbstractIntComparator() {
			public int compare( final int x, final int y ) {
				return size[ y ] - size[ x ];
			}
		} );
		final int[] copy = size.clone();
		for ( int i = size.length; i-- != 0; )
			size[ i ] = copy[ perm[ i ] ];
		Util.invertPermutationInPlace( perm );
		for ( int i = component.length; i-- != 0; )
			component[ i ] = perm[ component[ i ] ];
	}

	public static void main( String arg[] ) throws IOException, JSAPException {
		SimpleJSAP jsap = new SimpleJSAP( ConnectedComponents.class.getName(),
				"Computes the connected components of a symmetric graph of given basename. The resulting data is saved " +
						"in files stemmed from the given basename with extension .scc (a list of binary integers specifying the " +
						"component of each node) and .sccsizes (a list of binary integer specifying the size of each component).",
				new Parameter[] {
						new Switch( "sizes", 's', "sizes", "Compute component sizes." ),
						new Switch( "renumber", 'r', "renumber", "Renumber components in decreasing-size order." ),
						new FlaggedOption( "logInterval", JSAP.LONG_PARSER, Long.toString( ProgressLogger.DEFAULT_LOG_INTERVAL ), JSAP.NOT_REQUIRED, 'l', "log-interval",
								"The minimum time interval between activity logs in milliseconds." ),
						new UnflaggedOption( "basename", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "The basename of the graph." ),
						new UnflaggedOption( "resultsBasename", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The basename of the resulting files." ),
		}
				);

		JSAPResult jsapResult = jsap.parse( arg );
		if ( jsap.messagePrinted() ) System.exit( 1 );

		final String basename = jsapResult.getString( "basename" );
		final String resultsBasename = jsapResult.getString( "resultsBasename", basename );
		ProgressLogger pl = new ProgressLogger( LOGGER, jsapResult.getLong( "logInterval" ), TimeUnit.MILLISECONDS );

		final ConnectedComponents components = ConnectedComponents.compute( ImmutableGraph.loadOffline( basename ), pl );

		if ( jsapResult.getBoolean( "sizes" ) || jsapResult.getBoolean( "renumber" ) ) {
			final int size[] = components.computeSizes();
			if ( jsapResult.getBoolean( "renumber" ) ) components.sortBySize( size );
			if ( jsapResult.getBoolean( "sizes" ) ) BinIO.storeInts( size, resultsBasename + ".sccsizes" );
		}
		BinIO.storeInts( components.component, resultsBasename + ".scc" );
	}
}
