package com.base.kvstore;

public class Main {
    static public void call() {
        VectorClock c = new VectorClock();
        c.increments("node1");
        System.out.println(c.getClock());
    }

   static void main(String[] args) {
        call();

    }

}
