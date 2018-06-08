package com.hisense.etl.util.memoization;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Memoizer1<A,V> implements Computable<A,V> {

    private final Map<A,V> cache=new HashMap<A,V>();
    private final Computable<A, V> c;

    public Memoizer1(Computable<A, V> c){
        this.c=c;
    }

    public synchronized V compute(A arg) throws InterruptedException{
        V result=cache.get(arg);
        if(null==result){
            result = c.compute(arg);
            cache.put(arg,result);
        }
        return result;
    }
}
