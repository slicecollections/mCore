package tk.slicecollections.maxteer.bungee.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;
import tk.slicecollections.maxteer.bungee.Bungee;
import tk.slicecollections.maxteer.libraries.profile.InvalidMojangException;
import tk.slicecollections.maxteer.libraries.profile.Mojang;

/**
 * @author Maxter
 */
public class Listeners implements Listener {

  @EventHandler
  public void onServerConnected(ServerConnectedEvent evt) {
    ProxiedPlayer player = evt.getPlayer();

    if (Bungee.isFake(player.getName())) {
      // Enviar dados desse jogador que está utilizando Fake para o servidor processar.
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("FAKE");
      out.writeUTF(player.getName());
      out.writeUTF(Bungee.getFake(player.getName()));
      evt.getServer().sendData("mCore", out.toByteArray());

      // Caso o fake do jogador seja um nick original, tentaremos modificar a skin.
      LoginResult profile = ((InitialHandler) player.getPendingConnection()).getLoginProfile();
      if (profile != null) {
        try {
          String id = Mojang.getUUID(Bungee.getFake(player.getName()));

          // ID encontrado.
          if (id != null) {
            String textures = Mojang.getSkinProperty(id);

            // Skin encontrada.
            if (textures != null) {
              profile.setProperties(new LoginResult.Property[] {new LoginResult.Property(textures.split(" : ")[0], textures.split(" : ")[1], textures.split(" : ")[2])});
            }
          }
        } catch (InvalidMojangException ignore) {} // Aparentemente o jogador não é um nickname original válido.
      }
    }
  }
}
