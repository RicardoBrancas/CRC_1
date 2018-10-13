/*-
 * Copyright (c) 2013, Pedro Rijo
 * Copyright (c) 2013, Alexandre P Francisco
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

import it.unimi.dsi.stat.Jackknife;
import it.unimi.dsi.webgraph.algo.ApproximateNeighbourhoodFunctions;
import it.unimi.dsi.webgraph.algo.NeighbourhoodFunction;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedList;

public class ApproximateNeighbourhoodFunctionStats {

	public static void main(String[] args) {

		ObjectList<double[]> anfs = new ObjectArrayList<double[]>();
		for (int i = 0; i < args.length; i++) {
			
			LinkedList<String> values = new LinkedList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(args[i]));
				String line = null;
				while ((line = br.readLine()) != null)
					values.add(line);
			} catch (IOException e) {
				System.err.println(e);
			}

			double[] anf = new double[values.size()];
			Iterator<String> li = values.iterator();
			int k = 0;
			while (li.hasNext()) {
				anf[k] = Double.parseDouble(li.next()); 
				k++;
			}

			anfs.add(anf);
		}

		anfs = ApproximateNeighbourhoodFunctions.evenOut(anfs);
		Jackknife jack = Jackknife.compute(anfs, ApproximateNeighbourhoodFunctions.AVERAGE_DISTANCE);
		System.out.println("ADst:\t" + jack.estimate[0] + "\t(+/- " + jack.standardError[0] + ")");

		jack = Jackknife.compute(anfs, ApproximateNeighbourhoodFunctions.EFFECTIVE_DIAMETER);
		System.out.println("EDmt:\t" + jack.estimate[0] + "\t(+/- " + jack.standardError[0] + ")");

		jack = Jackknife.compute(anfs, ApproximateNeighbourhoodFunctions.HARMONIC_DIAMETER);
		System.out.println("HDmt:\t" + jack.estimate[0] + "\t(+/- " + jack.standardError[0] + ")");

		jack = Jackknife.compute(anfs, ApproximateNeighbourhoodFunctions.SPID);
		System.out.println("SPID:\t" + jack.estimate[0] + "\t(+/- " + jack.standardError[0] + ")");

		jack = Jackknife.compute(anfs, ApproximateNeighbourhoodFunctions.PMF);
		for (int i = 0; i < jack.estimate.length; i++)
			System.out.println("PMF " + i + " " + jack.estimate[i] + " " + jack.standardError[i]);

		jack = Jackknife.compute(anfs, ApproximateNeighbourhoodFunctions.CDF);
		for (int i = 0; i < jack.estimate.length; i++)
			System.out.println("CDF " + i + " " + jack.estimate[i] + " " + jack.standardError[i]);
	}
}
