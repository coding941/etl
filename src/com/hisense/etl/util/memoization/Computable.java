package com.hisense.etl.util.memoization;

public interface Computable<A,V> {
    V compute(A arg) throws InterruptedException;
}
