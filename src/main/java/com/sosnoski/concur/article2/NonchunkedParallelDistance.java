/*
 * Copyright (c) 2014, Dennis M. Sosnoski.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.sosnoski.concur.article2;

import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.sosnoski.concur.article1.DistancePair;
import com.sosnoski.concur.article1.TimingTestBase;

/**
 * Run timed test of finding best matches for misspelled words.
 */
public class NonchunkedParallelDistance extends TimingTestBase
{
    private final String[] knownWords;
    
    private final int blockSize;
    
    public NonchunkedParallelDistance(String[] words, int block) {
        knownWords = words;
        blockSize = block;
    }

	@Override
	public void shutdown() {}
	
	@Override
	public int blockSize() {
		return blockSize;
	}
    
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
    
    /**
     * Calculate edit distance from target to known word.
     *
     * @param target target word
     * @param known known word
     * @param v0 int array of length targetText.length() + 1
     * @param v1 int array of length targetText.length() + 1
     */
    private static int editDistance(String target, String known, int[] v0, int[] v1) {
        
        // initialize v0 (prior row of distances) as edit distance for empty 'word'
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }
        
        // calculate updated v0 (current row distances) from the previous row v0
        for (int i = 0; i < known.length(); i++) {
            
            // first element of v1 = delete (i+1) chars from target to match empty 'word'
            v1[0] = i + 1;
            
            // use formula to fill in the rest of the row
            for (int j = 0; j < target.length(); j++) {
                int cost = (known.charAt(i) == target.charAt(j)) ? 0 : 1;
                v1[j + 1] = minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
            }
            
            // swap v1 (current row) and v0 (previous row) for next iteration
            int[] hold = v0;
            v0 = v1;
            v1 = hold;
        }
        
        // return final value representing best edit distance
        return v0[target.length()];
    }
    
    /* (non-Javadoc)
     * @see com.sosnoski.concur1.TimedTest#bestMatch(java.lang.String)
     */
    @Override
    public DistancePair bestMatch(String target) {
        int size = target.length() + 1;
        Supplier<WordChecker> supplier = () -> new WordChecker(size);
        ObjIntConsumer<WordChecker> accumulator = (t, value) -> t.checkWord(target, knownWords[value]);
        BiConsumer<WordChecker, WordChecker> combiner = (t, u) -> t.merge(u);
        return IntStream.range(0, knownWords.length).parallel()
    		.collect(supplier, accumulator, combiner).result();
    }
    
    private static class WordChecker {
        protected final int[] v0;
        protected final int[] v1;
        protected int bestDistance = Integer.MAX_VALUE;
        protected String bestKnown = null;
        
        public WordChecker(int length) {
            v0 = new int[length];
            v1 = new int[length];
        }
        
        protected void checkWord(String target, String known) {
            int distance = editDistance(target, known, v0, v1);
            if (bestDistance > distance) {
                bestDistance = distance;
                bestKnown = known;
            } else if (bestDistance == distance) {
                bestKnown = null;
            }
        }
        
        protected void merge(WordChecker other) {
            if (bestDistance > other.bestDistance) {
                bestDistance = other.bestDistance;
                bestKnown = other.bestKnown;
            } else if (bestDistance == other.bestDistance) {
                bestKnown = null;
            }
        }
        
        protected DistancePair result() {
            return (bestKnown == null) ? new DistancePair(bestDistance) : new DistancePair(bestDistance, bestKnown);
        }
    }
}