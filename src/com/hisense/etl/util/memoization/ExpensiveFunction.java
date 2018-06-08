package com.hisense.etl.util.memoization;

import java.math.BigInteger;

public class ExpensiveFunction implements Computable<String,BigInteger> {

    public BigInteger compute(String arg) throws InterruptedException{
        //after ...
        return new BigInteger(arg);
    }
}
