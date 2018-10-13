/*-
 * Copyright (c) 2009, A P Francisco
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

public class DisjointSet {
	private int size;
	private int[] pi;
	private int[] rank;

	public DisjointSet(int n) {
		size = n + 1;
		pi = new int[size];
		rank = new int[size];

		for (int i = 0; i < size; i++) {
			rank[i] = 1;
			pi[i] = i;
		}
	}

	public int findSet(int i) {
		if (i < 0 || i >= size)
			return -1;
		for (; i != pi[i]; i = pi[i])
			pi[i] = pi[pi[i]];
		return i;
	}
	
	public boolean sameSet(int i, int j) {
		return findSet(i) == findSet(j);
	}

	public void unionSet(int i, int j) {
		if (i < 0 || j < 0 || i >= size || j >= size)
			return;
		int iRoot = findSet(i);
		int jRoot = findSet(j);
		if (iRoot == jRoot)
			return;
		if (rank[iRoot] > rank[jRoot]) {
			pi[jRoot] = iRoot;
			rank[iRoot] += rank[jRoot];
		} else if (rank[iRoot] < rank[jRoot]) {
			pi[iRoot] = jRoot;
			rank[jRoot] += rank[iRoot];
		} else {
			pi[iRoot] = jRoot;
			rank[jRoot] += rank[iRoot];
		}
	}
	
	public int getRank(int i) {
		return rank[i];
	}
}
