package tk.slicecollections.maxteer.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.enums.PrivateMessages;
import tk.slicecollections.maxteer.player.enums.ProtectionLobby;
import tk.slicecollections.maxteer.player.fake.FakeManager;
import tk.slicecollections.maxteer.player.hotbar.HotbarButton;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.plugin.logger.MLogger;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.reflection.acessors.FieldAccessor;
import tk.slicecollections.maxteer.servers.ServerItem;
import tk.slicecollections.maxteer.titles.TitleManager;
import tk.slicecollections.maxteer.utils.SliceUpdater;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static tk.slicecollections.maxteer.Core.warnings;

/**
 * @author Maxter
 */
public class Listeners implements Listener {

  public static final MLogger LOGGER = ((MLogger) Core.getInstance().getLogger()).getModule("Listeners");
  public static final Map<String, Long> DELAY_PLAYERS = new HashMap<>();
  private static final Map<String, Long> PROTECTION_LOBBY = new HashMap<>();

  private static final FieldAccessor<Map> COMMAND_MAP = Accessors.getField(SimpleCommandMap.class, "knownCommands", Map.class);
  private static final SimpleCommandMap SIMPLE_COMMAND_MAP = (SimpleCommandMap) Accessors.getMethod(Bukkit.getServer().getClass(), "getCommandMap").invoke(Bukkit.getServer());

  public static void setupListeners() {
    Bukkit.getPluginManager().registerEvents(new Listeners(), Core.getInstance());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent evt) {
    if (evt.getResult() == PlayerPreLoginEvent.Result.ALLOWED) {
      Profile.createOrLoadProfile(evt.getName());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerLoginMonitor(PlayerLoginEvent evt) {
    if (Profile.getProfile(evt.getPlayer().getName()) == null) {
      evt.disallow(PlayerLoginEvent.Result.KICK_OTHER,
        " \n§cAparentemente o servidor não conseguiu carregar seu Perfil.\n \n§cIsso ocorre normalmente quando o servidor ainda está despreparado para receber logins, aguarde um pouco e tente novamente.");
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass PlayerJoinEvent for ${n} v${v}", () -> {
      Player player = evt.getPlayer();
      if (player.hasPermission("mcore.admin")) {
        if (!warnings.isEmpty()) {
          TextComponent component = new TextComponent("");
          for (BaseComponent components : TextComponent.fromLegacyText(
            " \n §6§lAVISO IMPORTANTE\n \n §7Aparentemente você utiliza plugins que conflitam com os \"plugins m\", caso continue a usar estes plugins, não terá direito a suporte.\n \n §7Remova os seguintes plugins:")) {
            component.addExtra(components);
          }
          for (String warning : warnings) {
            for (BaseComponent components : TextComponent.fromLegacyText("\n§f" + warning)) {
              component.addExtra(components);
            }
          }
          for (BaseComponent components : TextComponent.fromLegacyText("\n ")) {
            component.addExtra(components);
          }

          player.spigot().sendMessage(component);
          EnumSound.VILLAGER_NO.play(player, 1.0F, 1.0F);
        }

        if (!ServerItem.WARNINGS.isEmpty()) {
          TextComponent component = new TextComponent("");
          for (BaseComponent components : TextComponent.fromLegacyText(
            " \n §6§lAVISO IMPORTANTE\n \n §7O sistema de servidores do mCore foi alterado nessa nova versão e, aparentemente você utiliza a versão antiga!\n §7O novo padrão de 'servernames' na servers.yml é 'IP:PORTA ; BungeeServerName' e você utiliza o antigo padrão 'BungeeServerName' nas seguintes entradas:")) {
            component.addExtra(components);
          }
          for (String warning : ServerItem.WARNINGS) {
            for (BaseComponent components : TextComponent.fromLegacyText("\n§f" + warning)) {
              component.addExtra(components);
            }
          }
          for (BaseComponent components : TextComponent.fromLegacyText("\n ")) {
            component.addExtra(components);
          }

          player.spigot().sendMessage(component);
          EnumSound.ORB_PICKUP.play(player, 1.0F, 1.0F);
        }

        if (SliceUpdater.UPDATER != null && SliceUpdater.UPDATER.canDownload) {
          TextComponent component = new TextComponent("");
          for (BaseComponent components : TextComponent
            .fromLegacyText(" \n §6§l[MCORE]\n \n §7O mCore possui uma nova atualização para ser feita, para prosseguir basta clicar ")) {
            component.addExtra(components);
          }
          TextComponent click = new TextComponent("AQUI");
          click.setColor(ChatColor.GREEN);
          click.setBold(true);
          click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mc atualizar"));
          click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para atualizar o mCore.")));
          component.addExtra(click);
          for (BaseComponent components : TextComponent.fromLegacyText("§7.\n ")) {
            component.addExtra(components);
          }

          player.spigot().sendMessage(component);
          EnumSound.LEVEL_UP.play(player, 1.0F, 1.0F);
        }
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass PlayerQuitEvent for ${n} v${v}", () -> {
      Profile profile = Profile.unloadProfile(evt.getPlayer().getName());
      if (profile != null) {
        if (profile.getGame() != null) {
          profile.getGame().leave(profile, profile.getGame());
        }
        TitleManager.leaveServer(profile);
        profile.save();
        profile.destroy();
      }

      if (FakeManager.isBungeeSide()) {
        FakeManager.removeFake(evt.getPlayer());
      }
      DELAY_PLAYERS.remove(evt.getPlayer().getName());
      PROTECTION_LOBBY.remove(evt.getPlayer().getName().toLowerCase());
    });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onAsyncPlayerChat(AsyncPlayerChatEvent evt) {
    if (evt.isCancelled()) {
      return;
    }

    Player player = evt.getPlayer();

    String format = String.format(evt.getFormat(), player.getName(), evt.getMessage());

    TextComponent component = new TextComponent("");
    for (BaseComponent components : TextComponent.fromLegacyText(format)) {
      component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getName() + " "));
      component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
        .fromLegacyText(Role.getColored(player.getName()) + "\n§fGrupo: " + Role.getPlayerRole(player).getName() + "\n \n§eClique para enviar uma mensagem privada.")));
      component.addExtra(components);
    }

    evt.setCancelled(true);
    evt.getRecipients().forEach(players -> {
      if (players != null) {
        Player.Spigot spigot = players.spigot();
        if (spigot != null) {
          spigot.sendMessage(component);
        }
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null) {
      String[] args = evt.getMessage().replace("/", "").split(" ");

      String command = args[0];
      if (COMMAND_MAP.get(SIMPLE_COMMAND_MAP).containsKey("lobby") && command.equals("lobby") && profile.getPreferencesContainer()
        .getProtectionLobby() == ProtectionLobby.ATIVADO) {
        long last = PROTECTION_LOBBY.getOrDefault(player.getName().toLowerCase(), 0L);
        if (last > System.currentTimeMillis()) {
          PROTECTION_LOBBY.remove(player.getName().toLowerCase());
          return;
        }

        evt.setCancelled(true);
        PROTECTION_LOBBY.put(player.getName().toLowerCase(), System.currentTimeMillis() + 3000);
        player.sendMessage("§aVocê tem certeza? Utilize /lobby novamente para voltar ao lobby.");
      } else if (COMMAND_MAP.get(SIMPLE_COMMAND_MAP).containsKey("tell") && args.length > 1 && command.equals("tell") && !args[1].equalsIgnoreCase(player.getName())) {
        profile = Profile.getProfile(args[1]);
        if (profile != null && profile.getPreferencesContainer().getPrivateMessages() != PrivateMessages.TODOS) {
          evt.setCancelled(true);
          player.sendMessage("§cEste usuário desativou as mensagens privadas.");
        }
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null && profile.getHotbar() != null) {
      ItemStack item = player.getItemInHand();
      if (evt.getAction().name().contains("CLICK") && item != null && item.hasItemMeta()) {
        HotbarButton button = profile.getHotbar().compareButton(player, item);
        if (button != null) {
          evt.setCancelled(true);
          button.getAction().execute(profile);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getWhoClicked() instanceof Player) {
      Player player = (Player) evt.getWhoClicked();
      Profile profile = Profile.getProfile(player.getName());

      if (profile != null && profile.getHotbar() != null) {
        ItemStack item = evt.getCurrentItem();
        if (item != null && item.getType() != Material.AIR) {
          if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(player.getInventory()) && item.hasItemMeta()) {
            HotbarButton button = profile.getHotbar().compareButton(player, item);
            if (button != null) {
              evt.setCancelled(true);
              button.getAction().execute(profile);
            }
          }
        }
      }
    }
  }
}
