package com.sosnoski.concur.article2;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CompletableFutureFail {
    
    private Supplier<Integer> newLambda(int i) {
        return () -> Integer.valueOf(i);
    }
    
    private Integer run(int n) {
        CompletableFuture<Integer> last = CompletableFuture.supplyAsync(newLambda(0));
        for (int i = 1; i < n; i++) {
            last = CompletableFuture.supplyAsync(newLambda(i)) .thenCombine(last, Math::max);
        }
        return last.join();
    }
    
    public static void main(String[] args) {
        CompletableFutureFail fail = new CompletableFutureFail();
        for (int i = 0; i < 100; i++) {
            fail.run(10000);
            System.out.println("Did it " + i);
        }
    }
}
