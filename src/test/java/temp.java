//package com.fabros.rawdatabi.coreinfra.nikich_test;
//
//import static com.fabros.rawdatabi.coreinfra.nikich_test.NumberApplication.isPrimeNumber;
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//
//class NumberApplicationTest {
//
//
//    @ParameterizedTest
//    @MethodSource("testPrime")
//    void testPrime(int n, boolean expectedResult) {
//        boolean result = isPrimeNumber(n);
//        assertEquals(expectedResult, result);
//    }
//
//    static Object[][] testPrime(){
//        return new Object[][]{
//                new Object[]{10, false},
//                new Object[]{3, true},
//                new Object[]{120, false},
//                new Object[]{7, true},
//                new Object[]{8, false},
//                new Object[]{5, true}
//        };
//    }
//
//    @ParameterizedTest
//    @MethodSource("testPrimeE")
//    void testPrimeE(int n, boolean expectedResult ) {
//        try {
//            isPrimeNumber(n);
//        }catch (Exception e){
//            assertTrue(e.getMessage().toLowerCase().contains("illegal"));
//            assertTrue( e instanceof IllegalArgumentException);
//            return;
//        }
//        fail("expected exception not thrown");
//
//    }
//
//    static Object[][] testPrimeE(){
//        return new Object[][]{
//                new Object[]{1, false},
//                new Object[]{0, true},
//                new Object[]{-10, false}
//
//        };
//    }
//}

//
//package com.fabros.rawdatabi.coreinfra.nikich_test;
//
//import org.springframework.data.util.Pair;
//
//public class NumberApplication {
//
//    public static void main(String[] args) {
//        int n = 10;
//        boolean result = isPrimeNumber(n);
//
//        System.out.printf("%d - is prime: %s%n", n, result);
//    }
//
//    static Pair<Boolean, Boolean> isPrimeNumberPairVersion(int n) {
//        return Pair.of(false, false);
//    }
//
//    static boolean isPrimeNumber(int n) {
//        if (n<2) {
//            throw new IllegalArgumentException(n + " is illegal: it should >= 2");}
//
//        int divider = 2;
//
//
//        return false;
//
//    }
//
//}