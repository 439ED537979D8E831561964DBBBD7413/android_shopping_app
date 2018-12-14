package course.android.shopping_example_app.Utills;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class for check input legality
 */

public class Check {
    private Check(){} // make it private to avoid instantiating

    private final static String ILLEGALREGEX = "[~#@*+%{}<>\\[\\]|\"\\_^]";
    private final static String LEGALNAMEREGEX = "^\\p{L}+(?: \\p{L}+)*$";


    public static boolean isName(String s){ // to be used for checking names like first name and last name
        return s.matches(LEGALNAMEREGEX);
    }

    public static boolean isLegalString(String s){ // to be used when checking for legal input
        Pattern pattern = Pattern.compile(ILLEGALREGEX);
        Matcher matcher = pattern.matcher(s);
        return !matcher.find();
    }

}
