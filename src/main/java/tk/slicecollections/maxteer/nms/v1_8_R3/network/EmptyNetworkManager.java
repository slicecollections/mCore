package tk.slicecollections.maxteer.nms.v1_8_R3.network;

import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;

import java.net.SocketAddress;

/**
 * @author Maxter
 */
public class EmptyNetworkManager extends NetworkManager {

  public EmptyNetworkManager() {
    super(EnumProtocolDirection.CLIENTBOUND);
    this.channel = new EmptyChannel();
    this.l = new SocketAddress() {
      private static final long serialVersionUID = 7794407580553892140L;
    };
  }
}
