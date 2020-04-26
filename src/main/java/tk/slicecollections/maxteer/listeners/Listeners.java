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
import tk.slicecollections.maxteer.plugin.logger.MLogger;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.titles.TitleManager;
import tk.slicecollections.maxteer.utils.SliceUpdater;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Maxter
 */
public class Listeners implements Listener {

  public static final MLogger LOGGER = ((MLogger) Core.getInstance().getLogger()).getModule("Listeners");
  public static final Map<String, Long> DELAY_PLAYERS = new HashMap<>();
  private static final Map<String, Long> PROTECTION_LOBBY = new HashMap<>();

  private final boolean blockTell, blockLobby;

  public Listeners() {
    Map<?, ?> map =
      Accessors.getField(SimpleCommandMap.class, "knownCommands", Map.class).get(Accessors.getMethod(Bukkit.getServer().getClass(), "getCommandMap").invoke(Bukkit.getServer()));
    blockTell = map.containsKey("tell");
    blockLobby = map.containsKey("lobby");
  }

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
      if (SliceUpdater.UPDATER != null && SliceUpdater.UPDATER.canDownload && player.hasPermission("mcore.admin")) {
        TextComponent component = new TextComponent("");
        for (BaseComponent components : TextComponent.fromLegacyText(" \n §6§l[MCORE]\n \n §7O mCore possui uma nova atualização para ser feita, para prosseguir basta clicar ")) {
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
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null) {
      String[] args = evt.getMessage().replace("/", "").split(" ");

      String command = args[0];
      if (blockLobby && command.equals("lobby") && profile.getPreferencesContainer().getProtectionLobby() == ProtectionLobby.ATIVADO) {
        long last = PROTECTION_LOBBY.getOrDefault(player.getName().toLowerCase(), 0L);
        if (last > System.currentTimeMillis()) {
          PROTECTION_LOBBY.remove(player.getName().toLowerCase());
          return;
        }

        evt.setCancelled(true);
        PROTECTION_LOBBY.put(player.getName().toLowerCase(), System.currentTimeMillis() + 3000);
        player.sendMessage("§aVocê tem certeza? Utilize /lobby novamente para voltar ao lobby.");
      } else if (blockTell && args.length > 1 && command.equals("tell") && !args[1].equalsIgnoreCase(player.getName())) {
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
