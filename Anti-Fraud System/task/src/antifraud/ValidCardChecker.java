package antifraud;

public class ValidCardChecker {

    public static boolean check(String cardNumber) {

        int sumOfOddPlaces = 0;
        int doubledSumOfEvenPlaces = 0;

        for (int i = 0; i < cardNumber.length(); i++) {
            int currentNumber = Character.digit(cardNumber.charAt(i), 10);
            if (i % 2 == 0) {
                currentNumber *= 2;

                if (currentNumber > 9) {
                    currentNumber = currentNumber % 10 + currentNumber / 10;
                }

                doubledSumOfEvenPlaces += currentNumber;
            } else {
                sumOfOddPlaces += currentNumber;
            }
        }

        return (sumOfOddPlaces + doubledSumOfEvenPlaces) % 10 == 0;
    }
}
