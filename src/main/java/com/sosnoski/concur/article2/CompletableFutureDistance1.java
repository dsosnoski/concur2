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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import com.sosnoski.concur.article1.DistancePair;
import com.sosnoski.concur.article1.TimingTestBase;

/**
 * Run timed test of finding best matches for misspelled words.
 */
public class CompletableFutureDistance1 extends TimingTestBase {
    private final List<ChunkDistanceChecker> chunkCheckers;

    private final int blockSize;

    public CompletableFutureDistance1(String[] words, int block) {
        blockSize = block;
        chunkCheckers = ChunkDistanceChecker.buildCheckers(words, block);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public int blockSize() {
        return blockSize;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.sosnoski.concur1.TimedTest#bestMatch(java.lang.String)
     */
    @Override
    public DistancePair bestMatch(String target) {
        AtomicReference<DistancePair> best = new AtomicReference<>(DistancePair.worstMatch());
        CountDownLatch latch = new CountDownLatch(chunkCheckers.size());
        for (ChunkDistanceChecker checker: chunkCheckers) {
            CompletableFuture.supplyAsync(() -> checker.bestDistance(target))
                .thenAccept(result -> {
                    best.accumulateAndGet(result, DistancePair::best);
                    latch.countDown();
                });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during calculations", e);
        }
        return best.get();
    }
}