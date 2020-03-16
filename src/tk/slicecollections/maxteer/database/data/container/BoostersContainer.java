package tk.slicecollections.maxteer.database.data.container;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tk.slicecollections.maxteer.booster.Booster;
import tk.slicecollections.maxteer.booster.Booster.BoosterType;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.interfaces.AbstractContainer;

@SuppressWarnings("unchecked")
public class BoostersContainer extends AbstractContainer {

  public BoostersContainer(DataContainer dataContainer) {
    super(dataContainer);
    JSONObject boosters = this.dataContainer.getAsJsonObject();
    if (!boosters.containsKey("0")) {
      boosters.put("0", new JSONArray());
      boosters.put("1", new JSONArray());
      boosters.put("2", "none");
    }
    this.dataContainer.set(boosters.toString());
  }

  public boolean enable(Booster booster) {
    if (this.getEnabled() != null) {
      return false;
    }

    this.removeBooster(BoosterType.PRIVATE, booster);
    JSONObject boosters = this.dataContainer.getAsJsonObject();
    boosters.put("2", booster.getMultiplier() + ":" + (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(booster.getHours())));
    this.dataContainer.set(boosters.toString());
    boosters.clear();
    boosters = null;
    return true;
  }

  public void addBooster(BoosterType type, double multiplier, long hours) {
    JSONObject boosters = this.dataContainer.getAsJsonObject();
    ((JSONArray) boosters.get(String.valueOf(type.ordinal()))).add(multiplier + ":" + hours);
    this.dataContainer.set(boosters.toString());
    boosters.clear();
    boosters = null;
  }

  public void removeBooster(BoosterType type, Booster booster) {
    JSONObject boosters = this.dataContainer.getAsJsonObject();
    ((JSONArray) boosters.get(String.valueOf(type.ordinal()))).remove(booster.getMultiplier() + ":" + booster.getHours());
    this.dataContainer.set(boosters.toString());
    boosters.clear();
    boosters = null;
  }

  public List<Booster> getBoosters(BoosterType type) {
    List<Booster> list = new ArrayList<>();
    JSONArray boosters = (JSONArray) this.dataContainer.getAsJsonObject().get(String.valueOf(type.ordinal()));
    for (Object obj : boosters) {
      if (obj instanceof String) {
        list.add(Booster.parseBooster(type, (String) obj));
      }
    }

    return list;
  }

  public String getEnabled() {
    JSONObject boosters = this.dataContainer.getAsJsonObject();
    String current = (String) boosters.get("2");
    if (current.equals("none")) {
      boosters.clear();
      boosters = null;
      return null;
    }

    String[] splitted = current.split(":");
    double multiplier = Double.parseDouble(splitted[0]);
    long expires = Long.parseLong(splitted[1]);
    if (expires > System.currentTimeMillis()) {
      boosters.clear();
      boosters = null;
      return multiplier + ":" + expires;
    }

    boosters.put("2", "none");
    this.dataContainer.set(boosters.toString());
    boosters.clear();
    boosters = null;
    return null;
  }
}
