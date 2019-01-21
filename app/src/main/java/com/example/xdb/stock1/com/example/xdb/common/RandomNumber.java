package com.example.xdb.stock1.com.example.xdb.common;

import java.text.DecimalFormat;

public class RandomNumber {
    public static String getRandomNumber(int length){
        StringBuilder randomNumber = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0");
        for(int i = 0 ; i < length; i++){
            int num = Integer.parseInt(df.format(Math.floor(Math.random() * 10))) ;
            randomNumber.append(num);
        }
        return randomNumber.toString();
    }

    public static void main(String[] args) {
        for(int i = 0 ; i < 10; i++) {
            System.out.println(getRandomNumber(i));
        }
    }
}
