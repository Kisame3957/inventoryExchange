package inventoryex;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.StringBufferInputStream;
import java.util.*;

public final class InventoryEx extends JavaPlugin implements Listener {

    public Map<UUID, ItemStack[]> inventories = (Map) new HashMap<>();

    int invvalue;
    String perm;
    TabCompleter tab;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("[inventoryEx] " + ChatColor.GREEN + "inventoryEx Plugin Enabled");
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        if (!getConfig().contains("inventoryExConfig")) {
            getConfig().set("inventoryExConfig.invmaxvalue", 5);
            getConfig().set("inventoryExConfig.permissions", "None");
            saveConfig();
        }
        invvalue = (int) getConfig().getInt("inventoryExConfig.invmaxvalue");
        perm = (String) getConfig().getString("inventoryExConfig.permissions","None");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[inventoryEx] " + ChatColor.RED + "inventoryEx Plugin Disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        reloadConfig();
        Player p = (Player) sender;
        //if(!(perm == "None")){
        //    if (sender.hasPermission(perm)) {
        //        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.RED + "あなたはこのコマンドを使用する権限を持っていません");
        //        return true;
        //    }
        //}
        if(args.length == 0) {
            sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "-----------" + ChatColor.RESET + ChatColor.GRAY + "[" + ChatColor.AQUA +"inventoryEx" + ChatColor.GRAY + "]" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + "-----------");
            for(int i = 1; i <= invvalue; i++) {
                if(getConfig().contains(p.getName() + ".inventoryList" + ".name" + i)) {
                    sender.sendMessage(ChatColor.YELLOW + "inv" + i + ": " + ChatColor.AQUA + "[" + ChatColor.GREEN + getConfig().getString(p.getName() + ".inventoryList" + ".name" + i) + ChatColor.AQUA + "]");
                } else if(getConfig().contains(p.getName() + ".inventoryList" + ".-------------------------inv" + i + "-------------------------")) {
                    sender.sendMessage(ChatColor.YELLOW + "inv" + i + ": " + ChatColor.AQUA + "[Already set]");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "inv" + i + ": " + ChatColor.RED + "[Not set]");
                }
            }
            sender.sendMessage(ChatColor.AQUA + "inventoryExの使い方については/ie helpを参照してください");
            sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "---------------------------------");
            return true;
        }
        if(args[0].equalsIgnoreCase("save")) {
            if(StringUtils.isNumeric(args[1])){
                if(Integer.parseInt(args[1]) <= invvalue) {
                    int snum = Integer.parseInt(args[1]);
                    this.inventories.put(p.getUniqueId(), p.getInventory().getContents());
                    ItemStack[] invArray = p.getInventory().getContents();
                    getConfig().set(p.getName() + ".inventoryList" + ".-------------------------inv" + snum + "-------------------------", invArray);
                    saveConfig();
                    sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.GREEN + "inv" + snum + "に現在のインベントリをセーブしました");
                }
            } else {
                sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.RED + "セーブしたい番号を指定してください");
            }
        } else if(args[0].equalsIgnoreCase("load")) {
            if (StringUtils.isNumeric(args[1])) {
                if (Integer.parseInt(args[1]) <= invvalue) {
                    int lnum = Integer.parseInt(args[1]);
                    List<ItemStack> invList = (List<ItemStack>) getConfig().get(p.getName() + ".inventoryList" + ".-------------------------inv" + lnum + "-------------------------");
                    ItemStack[] inv = invList.toArray(new ItemStack[invList.size()]);
                    this.inventories.put(p.getPlayer().getUniqueId(), inv);
                    p.getInventory().setContents(this.inventories.get(p.getUniqueId()));
                    if(getConfig().contains(p.getName() + ".inventoryList" + ".name" + lnum)){
                        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.GREEN + "inv" + lnum + "[" + ChatColor.AQUA + getConfig().getString(p.getName() + ".inventoryList" + ".name" + lnum) + ChatColor.GREEN +"]のインベントリをロードしました");
                    } else {
                        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.GREEN + "inv" + lnum + "のインベントリをロードしました");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.RED + "ロードしたい番号を指定してください");
            }
        } else if(args[0].equalsIgnoreCase("setname")){
            if(StringUtils.isNumeric(args[1])){
                if(Integer.parseInt(args[1]) <= invvalue){
                    int nnum = Integer.parseInt(args[1]);
                    if(!(args[2] == null)) {
                        getConfig().set(p.getName() + ".inventoryList" + ".name" + nnum, args[2]);
                        saveConfig();
                        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.GREEN + "inv" + nnum + "に名前[" + ChatColor.AQUA + args[2] + ChatColor.GREEN + "]をセットしました");
                    } else {
                        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.RED + "セットしたい名前を入力してください");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.RED + "名前をセットしたい番号を指定してください");
            }
        } else if(args[0].equalsIgnoreCase("help")){
            sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "---------" + ChatColor.RESET + ChatColor.GRAY + "[" + ChatColor.AQUA +"inventoryEx Help" + ChatColor.GRAY + "]" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + "---------");
            sender.sendMessage(ChatColor.YELLOW + "/ie :");
            sender.sendMessage(ChatColor.AQUA + "保存できるインベントリのリストを表示します");
            sender.sendMessage(ChatColor.YELLOW + "/ie save [セーブしたいインベントリの番号] :");
            sender.sendMessage(ChatColor.AQUA + "インベントリを指定したインベントリの番号に保存します");
            sender.sendMessage(ChatColor.YELLOW + "/ie load [ロードしたいインベントリの番号] :");
            sender.sendMessage(ChatColor.AQUA + "保存したインベントリを指定したインベントリの番号から呼び出します");
            sender.sendMessage(ChatColor.YELLOW + "/ie setname [名前をセットしたい番号] [セットしたい名前] :");
            sender.sendMessage(ChatColor.AQUA + "指定したインベントリの番号に名前を付けます");
            sender.sendMessage(ChatColor.YELLOW + "/ie reload :");
            sender.sendMessage(ChatColor.AQUA + "inventoryExのconfigをリロードします");
            sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "----------------------------------");
        } else if(args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            invvalue = (int) getConfig().getInt("inventoryExConfig.invmaxvalue");
            perm = (String) getConfig().getString("inventoryExConfig.permissions","None");
            sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.GREEN + "inventoryExのconfigのリロードが完了しました");
        } else {
            sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "invEx" + ChatColor.GRAY + "] " + ChatColor.RED + "不明なコマンドです。/ie helpを参照してください");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        if (args.length == 1) {
                commands.add("save");
                commands.add("load");
                commands.add("setname");
                commands.add("help");
                commands.add("reload");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equals("save")) {
                for(int i = 1;i<=invvalue;i++) {
                    commands.add("" + i);
                }
            }
            if (args[0].equals("load")) {
                for(int i = 1;i<=invvalue;i++) {
                    commands.add("" + i);
                }
            }
            if (args[0].equals("setname")) {
                for(int i = 1;i<=invvalue;i++) {
                    commands.add("" + i);
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            if(args[2].equals("setname")) {
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

    public static void sendHoverText(Player p, String text, String hoverText, String command, String suggest) {
        HoverEvent hoverEvent = null;
        if (hoverText != null) {
            BaseComponent[] hover = new ComponentBuilder(hoverText).create();
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover);
        }
        ClickEvent clickEvent = null;
        if (command != null) {
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        } else if (suggest != null) {
            clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest);
        }
        BaseComponent[] message = new ComponentBuilder(text).event(hoverEvent).event(clickEvent).create();
        p.spigot().sendMessage(message);
    }
}
