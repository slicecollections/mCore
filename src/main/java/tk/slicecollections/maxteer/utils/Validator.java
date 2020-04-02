package tk.slicecollections.maxteer.utils;

import java.util.regex.Pattern;

public class Validator {

  private static Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9_]+$");

  public static boolean isValidUsername(String name) {
    return VALID_USERNAME.matcher(name).matches();
  }
}
