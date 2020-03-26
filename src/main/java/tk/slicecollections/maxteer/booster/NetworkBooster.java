package tk.slicecollections.maxteer.booster;

/**
 * @author Maxter
 */
public class NetworkBooster {

  private String booster;
  private double multiplier;
  private long expires;

  public NetworkBooster(String booster, double multiplier, long expires) {
    this.booster = booster;
    this.multiplier = multiplier;
    this.expires = expires;
  }

  public void gc() {
    this.booster = null;
    this.multiplier = 0.0D;
    this.expires = 0L;
  }

  public String getBooster() {
    return this.booster;
  }

  public double getMultiplier() {
    return this.multiplier;
  }

  public long getExpires() {
    return this.expires;
  }
}
