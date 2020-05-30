package tk.slicecollections.maxteer.servers;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Maxter
 */
public class ServerPing {

  private int timeout;
  private JsonObject json;
  private InetSocketAddress host;

  public ServerPing(InetSocketAddress address) {
    this(address, 1000);
  }

  public ServerPing(InetSocketAddress address, int timeout) {
    this.host = address;
    this.timeout = timeout;
  }

  public ServerPing(String ip, int port) {
    this(ip, port, 1000);
  }

  public ServerPing(String ip, int port, int timeout) {
    this(new InetSocketAddress(ip, port), timeout);
  }

  public void fetch() {
    try (Socket socket = new Socket()) {
      socket.setSoTimeout(timeout);
      socket.connect(host, timeout);

      DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

      ByteArrayOutputStream b = new ByteArrayOutputStream();

      DataOutputStream handshake = new DataOutputStream(b);
      handshake.writeByte(0x00);
      writeVarInt(handshake, 47);
      writeVarInt(handshake, host.getHostString().length());

      handshake.writeBytes(host.getHostString());
      handshake.writeShort(host.getPort());
      writeVarInt(handshake, 1);

      writeVarInt(dataOutputStream, b.size());
      dataOutputStream.write(b.toByteArray());

      dataOutputStream.writeByte(0x01);
      dataOutputStream.writeByte(0x00);
      DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
      readVarInt(dataInputStream);

      int id = readVarInt(dataInputStream);
      if (id == -1) {
        throw new IOException("Error requesting ServerPing -> " + host);
      }

      int length = readVarInt(dataInputStream);
      if (length == -1) {
        throw new IOException("Error requesting ServerPing -> " + host);
      }

      byte[] in = new byte[length];

      dataInputStream.readFully(in);
      String json = new String(in);
      long now = System.currentTimeMillis();
      dataOutputStream.writeByte(0x09);
      dataOutputStream.writeByte(0x01);
      dataOutputStream.writeLong(now);

      readVarInt(dataInputStream);
      id = readVarInt(dataInputStream);
      if (id == -1) {
        throw new IOException("Error requesting ServerPing -> " + host);
      }

      this.json = new JsonParser().parse(json).getAsJsonObject();
      dataOutputStream.close();
      processData();
    } catch (Exception ex) {
      this.motd = null;
      this.online = 0;
      this.max = 0;
    }
  }

  private void processData() {
    JsonObject players = json.get("players").getAsJsonObject();

    this.motd = json.get("description").getAsString();
    this.max = players.get("max").getAsInt();
    this.online = players.get("online").getAsInt();
  }

  private String motd = null;
  private int max = 0;
  private int online = 0;

  private int readVarInt(DataInputStream in) throws IOException {
    int i = 0, j = 0;
    while (true) {
      int k = in.readByte();
      i |= (k & 0x7F) << j++ * 7;
      if (j > 5) {
        throw new RuntimeException("Int be higher");
      }

      if ((k & 0x80) != 128) {
        break;
      }
    }
    return i;
  }

  private void writeVarInt(DataOutputStream out, int value) throws IOException {
    while (true) {
      if ((value & 0xFFFFFF80) == 0) {
        out.writeByte(value);
        return;
      }

      out.writeByte(value & 0x7F | 0x80);
      value >>>= 7;
    }
  }
  
  public String getMotd() {
    return this.motd;
  }
  
  public int getMax() {
    return this.max;
  }
  
  public int getOnline() {
    return this.online;
  }

  public String toString() {
    return "ServerPing{motd=" + motd + ", online=" + online + ", max=" + max + "}";
  }
}
