package tk.slicecollections.maxteer.utils;

import java.util.Calendar;

public class TimeUtils {

  public static boolean isNewYear() {
    Calendar cl = Calendar.getInstance();
    return (cl.get(2) == 11 && cl.get(5) == 31) || (cl.get(2) == 0 && cl.get(5) == 1);
  }

  public static boolean isChristmas() {
    Calendar cl = Calendar.getInstance();
    return cl.get(2) == 11 && (cl.get(5) == 24 || cl.get(5) == 25);
  }

  public static int getLastDayOfMonth(int month) {
    Calendar cl = Calendar.getInstance();
    cl.set(2, month - 1);
    return cl.getActualMaximum(5);
  }

  public static int getLastDayOfMonth() {
    return Calendar.getInstance().getActualMaximum(5);
  }

  public static long getExpireIn(int days) {
    Calendar cooldown = Calendar.getInstance();
    cooldown.set(Calendar.HOUR, 24);
    for (int day = 0; day < days - 1; day++) {
      cooldown.add(Calendar.HOUR, 24);
    }
    cooldown.set(Calendar.MINUTE, 0);
    cooldown.set(Calendar.SECOND, 0);

    return cooldown.getTimeInMillis();
  }
  
  public static String getTimeUntil(long epoch) {
    epoch -= System.currentTimeMillis();
    return getTime(epoch);
  }

  public static String getTime(long time) {
    long ms = time / 1000;
    if (ms <= 0) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    long days = ms / 86400;
    if (days > 0) {
      result.append(days + " " + (days > 1 ? "dias" : "dia"));
      ms -= days * 86400;
      if (ms / 3600 > 0) {
        result.append(", ");
      }
    }
    long hours = ms / 3600;
    if (hours > 0) {
      result.append(hours + " " + (hours > 1 ? "horas" : "hora"));
      ms -= hours * 3600;
      if (ms / 60 > 0) {
        result.append(", ");
      }
    }
    long minutes = ms / 60;
    if (minutes > 0) {
      result.append(minutes + " " + (minutes > 1 ? "minutos" : "minuto"));
      ms -= minutes * 60;
    }

    return result.toString();
  }
}
