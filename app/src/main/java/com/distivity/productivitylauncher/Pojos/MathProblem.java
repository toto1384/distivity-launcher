package com.distivity.productivitylauncher.Pojos;

import java.util.Random;

public class MathProblem {

    private String problem;

    private String correctAnswer;

    private static MathProblem mathProblem;

    public static MathProblem getMathProblem(){

        if (mathProblem==null){
            mathProblem=getRandomMathProblem();
        }

        return mathProblem;
    }

    public static void releaseMathProblem(){
        mathProblem=null;
    }


    private MathProblem(String problem, String correctAnswer) {
        this.problem = problem;
        this.correctAnswer = correctAnswer;

    }

    public String getProblem() {
        return problem;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    private static int getRandomNumber(){
        Random rand = new Random();

        return rand.nextInt((99 - 1) + 1) + 1;
    }

    private static MathProblem getAddingProblem() {

        int rn1 = getRandomNumber();
        int rn2 = getRandomNumber();

        return new MathProblem(rn1 + "+" + rn2,String.valueOf(rn1+rn2));
    }

    private static MathProblem getSubstractingProblem(){
        int rn1 =getRandomNumber();
        int rn2 = getRandomNumber();

        if (rn1 < rn2){
            return new MathProblem(rn2 + "-" + rn1,String.valueOf(rn2 -rn1));
        }else{
            return new MathProblem(rn1 + "-" + rn2,String.valueOf(rn1 -rn2));
        }
    }

    private static MathProblem getRandomMathProblem() {

        switch ((int) (Math.random() *2)){
            case 0 :return getAddingProblem();
            case 1 :return getSubstractingProblem();
        }
        return null;
    }

}
