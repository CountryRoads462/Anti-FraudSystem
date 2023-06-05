package antifraud;

public class ValidIPAddressChecker {

    public static boolean check(String ipAddress) {

        String[] partsOfAddress = ipAddress.split("\\.");

        if (partsOfAddress.length != 4) {
            return false;
        }

        for (String part :
                partsOfAddress) {
            int num = Integer.parseInt(part);

            if (num < 0 || num > 255) {
                return false;
            }
        }

        return true;
    }
}
