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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.sosnoski.concur.article1.DistancePair;

public class ChunkDistanceChecker {
	
    private final String[] knownWords;

    public ChunkDistanceChecker(String[] knowns) {
    	knownWords = knowns;
    }

	/**
	 * Build list of checkers spanning word list.
	 * 
	 * @param words
	 * @param block
	 * @return checkers
	 */
	public static List<ChunkDistanceChecker> buildCheckers(String[] words, int block) {
		List<ChunkDistanceChecker> checkers = new ArrayList<>();
        for (int base = 0; base < words.length; base += block) {
        	int length = Math.min(block, words.length - base);
        	checkers.add(new ChunkDistanceChecker(Arrays.copyOfRange(words, base, base + length)));
        }
        return checkers;
	}
    
    private int minimum(int a, int b, int c) {
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
    private int editDistance(String target, String known, int[] v0, int[] v1) {
        
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

    /**
     * Find best distance from target to any known word.
     * 
     * @param target
     * @return best
     */
    public DistancePair bestDistance(String target) {
        int[] v0 = new int[target.length() + 1];
        int[] v1 = new int[target.length() + 1];
        int bestIndex = -1;
        int bestDistance = Integer.MAX_VALUE;
        boolean single = false;
        for (int i = 0; i < knownWords.length; i++) {
            int distance = editDistance(target, knownWords[i], v0, v1);
            if (bestDistance > distance) {
                bestDistance = distance;
                bestIndex = i;
                single = true;
            } else if (bestDistance == distance) {
                single = false;
            }
        }
        return single ? new DistancePair(bestDistance, knownWords[bestIndex]) :
        	new DistancePair(bestDistance);
    }

    /**
     * Find best distance from target to any known word.
     * 
     * @param target
     * @return best
     */
    public DistancePair bestDistance2(String target) {
        int[] v0 = new int[target.length() + 1];
        int[] v1 = new int[target.length() + 1];
        DistancePair best = DistancePair.worstMatch();
        for (int i = 0; i < knownWords.length; i++) {
            best = DistancePair.best(best, new DistancePair(editDistance(target, knownWords[i], v0, v1), knownWords[i]));
        }
        return best;
    }

    /**
     * Find best distance from target to any known word.
     * 
     * @param target
     * @return best
     */
    public DistancePair bestDistance3(String target) {
        int[] v0 = new int[target.length() + 1];
        int[] v1 = new int[target.length() + 1];
        return IntStream.range(0, knownWords.length)
            .mapToObj((i) -> new DistancePair(editDistance(target, knownWords[i], v0, v1), knownWords[i]))
            .reduce(DistancePair.worstMatch(), (a, b) -> DistancePair.best(a, b));
    }
}