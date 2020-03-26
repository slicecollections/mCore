package tk.slicecollections.maxteer.libraries.npclib.npc.profile.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tk.slicecollections.maxteer.libraries.npclib.npc.profile.Mojang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Maxter
 */
public class MojangAPI extends Mojang {

  private boolean response;

  @Override
  public String fetchId(String name) {
    this.response = false;
    try {
      URLConnection conn = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
      conn.setConnectTimeout(5000);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      this.response = true;
      StringBuilder builder = new StringBuilder();
      String read;
      while ((read = reader.readLine()) != null) {
        builder.append(read);
      }
      return builder.length() == 0 ? null : new JsonParser().parse(builder.toString()).getAsJsonObject().get("id").getAsString();
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String fetchSkinProperty(String id) {
    this.response = false;
    try {
      URLConnection conn = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id + "?unsigned=false").openConnection();
      conn.setConnectTimeout(5000);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      this.response = true;
      StringBuilder builder = new StringBuilder();
      String read;
      while ((read = reader.readLine()) != null) {
        builder.append(read);
      }
      String property = null;
      if (builder.length() != 0) {
        JsonObject properties = new JsonParser().parse(builder.toString()).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String name = properties.get("name").getAsString();
        String value = properties.get("value").getAsString();
        String signature = properties.get("signature").getAsString();
        property = name + " : " + value + " : " + signature;
      }
      return builder.length() == 0 ? null : property;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public boolean getResponse() {
    return response;
  }
}
