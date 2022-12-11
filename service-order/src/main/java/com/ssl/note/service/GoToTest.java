package com.ssl.note.service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/11 16:06
 * @Describe:
 */
public class GoToTest {
    static void notRetryContinueTest() {
        System.out.println("执行notRetryTest:");
        int i = 0, j = 0;
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 5; j++) {
                System.out.println(j);
                if (j == 3) {
                    continue;
                }
            }
        }
        System.out.printf("after loop, i = %d, j=%d", i, j);
        System.out.println();
    }

    static void retryContinue() {
        System.out.println("执行retryContinue:");
        int i = 0, j = 0;
        retry:
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 5; j++) {
                System.out.println(j);
                if (j == 3) {
                    continue retry;
                }
            }
        }
        System.out.printf("after loop, i = %d, j=%d", i, j);
        System.out.println();

    }

    static void notRetryBreakTest() {
        System.out.println("执行notRetryBreakTest:");
        int i = 0, j = 0;
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 5; j++) {
                System.out.println(j);
                if (j == 3) break;
            }

        }
        System.out.printf("after loop, i = %d, j=%d\n", i, j);
        System.out.println();

    }

    static void retryBreak() {
        System.out.println("执行retryBreak:");
        int i = 0, j = 0;
        retry1:
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 5; j++) {
                System.out.println(j);
                if (j == 3) break retry1;
            }

        }
        System.out.printf("after loop, i = %d, j=%d\n", i, j);
        System.out.println();

    }

    static void whileLabel() {
        System.out.println("执行whileLabel:");
        int i = 0;
        int j = 0;
        whileLabel:
        while (i++ < 10) {
            while (j < 10) {
                System.out.println(j);
                if (j == 6) break whileLabel;
                ++j;
            }
        }
        System.out.printf("after loop, i = %d, j=%d\n", i, j);
        System.out.println();
    }

    public static void main(String[] args) {
        notRetryContinueTest();
        System.out.println("***********************");
        retryContinue();
        System.out.println("***********************");
        notRetryBreakTest();
        System.out.println("***********************");
        retryBreak();
        System.out.println("***********************");
        whileLabel();
    }
}
