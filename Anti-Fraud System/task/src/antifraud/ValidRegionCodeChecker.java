package antifraud;

public class ValidRegionCodeChecker {

    public static boolean check(String code) {
        return code.matches("(" +
                "EAP|" +
                "ECA|" +
                "HIC|" +
                "LAC|" +
                "MENA|" +
                "SA|" +
                "SSA" +
                ")"
        );
    }
}
