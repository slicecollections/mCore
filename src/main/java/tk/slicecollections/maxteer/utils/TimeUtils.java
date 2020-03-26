package tk.slicecollections.maxteer.utils;

import java.util.Calendar;

/**
 * Classe com utilitários relacionados a TimeMillis e {@link Calendar}.
 *
 * @author Maxter
 */
public class TimeUtils {

  /**
   * Analisa se a data atual é de ano novo.
   *
   * @return TRUE caso seja, FALSE caso não.
   */
  public static boolean isNewYear() {
    Calendar cl = Calendar.getInstance();
    return (cl.get(Calendar.MONTH) == Calendar.DECEMBER && cl.get(Calendar.DATE) == 31) || (cl.get(Calendar.MONTH) == Calendar.JANUARY && cl.get(Calendar.DATE) == 1);
  }

  /**
   * Analisa se a data atual é de natal.
   *
   * @return TRUE caso seja, FALSE caso não.
   */
  public static boolean isChristmas() {
    Calendar cl = Calendar.getInstance();
    return cl.get(Calendar.MONTH) == Calendar.DECEMBER && (cl.get(Calendar.DATE) == 24 || cl.get(Calendar.DATE) == 25);
  }

  /**
   * Requesita a última data do mês a partir do número dele.
   *
   * @param month mês de 1 a 12
   * @return o último dia do mês escolhido.
   */
  public static int getLastDayOfMonth(int month) {
    Calendar cl = Calendar.getInstance();
    cl.set(Calendar.MONTH, month - 1);
    return cl.getActualMaximum(Calendar.DATE);
  }

  /**
   * Requesita a última data do mês atual.
   *
   * @return o último dia do mês.
   */
  public static int getLastDayOfMonth() {
    return Calendar.getInstance().getActualMaximum(Calendar.DATE);
  }

  /**
   * Cria uma data a partir da atual para expirar depois dos dias requesitados.
   *
   * @param days Quantia de dias a partir de hoje.
   * @return A data de expiração.
   */
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

  /**
   * Pega quanto tempo resta entre um timemillis e o atual.<br/>
   * Observação: encurtador de {@link #getTimeUntil(long, boolean)} com seconds ativado.
   *
   * @param epoch Timemillis para pegar o tempo restante.
   * @return O tempo restante ou vazio (<code>""</code>) caso já tenha passado do tempo.
   */
  public static String getTimeUntil(long epoch) {
    return getTimeUntil(epoch, true);
  }

  /**
   * Pega quanto tempo resta entre um timemillis e o atual.
   *
   * @param epoch   Timemillis para pegar o tempo restante.
   * @param seconds Se irá mostrar os segundos no tempo restante.
   * @return O tempo restante ou vazio (<code>""</code>) caso já tenha passado do tempo.
   */
  public static String getTimeUntil(long epoch, boolean seconds) {
    epoch -= System.currentTimeMillis();
    return getTime(epoch, seconds);
  }

  /**
   * Transforma um timemillis em String para saber quantos dias, horas, minutos e segundos há no timemillis.<br/>
   * Observação: encurtador de {@link #getTime(long, boolean)} com seconds ativado.
   *
   * @param time Timemillis para pegar o tempo.
   * @return O tempo em String ou vazio (<code>""</code>) caso <code>time <= 0</code>
   */
  public static String getTime(long time) {
    return getTime(time, true);
  }

  /**
   * Transforma um timemillis em String para saber quantos dias, horas, minutos e segundos há no timemillis.
   *
   * @param time    Timemillis para pegar o tempo.
   * @param seconds Se irá mostrar os segundos no tempo.
   * @return O tempo em String ou vazio (<code>""</code>) caso <code>time <= 0</code>
   */
  public static String getTime(long time, boolean seconds) {
    long ms = time / 1000;
    if (ms <= 0) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    long days = ms / 86400;
    if (days > 0) {
      result.append(days).append("d");
      ms -= days * 86400;
      if (ms / 3600 > 0) {
        result.append(" ");
      }
    }
    long hours = ms / 3600;
    if (hours > 0) {
      result.append(hours).append("h");
      ms -= hours * 3600;
      if (ms / 60 > 0) {
        result.append(" ");
      }
    }
    long minutes = ms / 60;
    if (minutes > 0) {
      result.append(minutes).append("m");
      ms -= minutes * 60;
      if (ms > 0 && seconds) {
        result.append(" ");
      }
    }
    if (seconds) {
      if (ms > 0) {
        result.append(ms).append("s");
      }
    }

    return result.toString();
  }
}
