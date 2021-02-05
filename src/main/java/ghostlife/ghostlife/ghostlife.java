package ghostlife.ghostlife;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public final class ghostlife extends JavaPlugin implements Listener {

    private static ghostlife instance;

    public static ghostlife getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("ghostlifereload")) {
            if (!sender.hasPermission("set.op")) {
                sender.sendMessage("コマンドを実行出来る権限がありません。");
                return true;
            }
            reloadConfig();
            getLogger().info("configリロードしました");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[server] &eghostlifePL configリロードしました"));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("adddamege")) {
            if (!sender.hasPermission("set.op")) {
                sender.sendMessage("コマンドを実行出来る権限がありません。");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("コマンドを正しく入力してください");
                return true;
            } else {
                int damage = 0;
                try {
                    damage = Integer.parseInt(args[0]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("ダメージ量は数値で指定してください");
                }
                Player target = null;
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("ゲーム内から実行してください");
                        return true;
                    }
                    target = (Player) sender;
                } else {
                    Player tar = Bukkit.getPlayer(args[1]);
                    if (tar == null || !tar.isOnline()) {
                        sender.sendMessage("指定されたプレイヤーはオンラインではありません");
                        return true;
                    }
                    target = tar;
                }
                target.damage(damage);
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("addCustomModel") || cmd.getName().equalsIgnoreCase("acm")) {
            if (!sender.hasPermission("set.op")) {
                sender.sendMessage("コマンドを実行出来る権限がありません。");
                return true;
            }
            if (args.length <= 0) {
                sender.sendMessage("コマンドを正しく入力してください");
                return false;
            }
            try{
                ItemStack item = p.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                String target = args[0];
                int id = Integer.parseInt(target);
                assert meta != null;
                meta.setCustomModelData(id);
                item.setItemMeta(meta);
                p.sendMessage("カスタムモデルデータ値を" + id + "に設定しました");
                return true;
            } catch(NullPointerException | NumberFormatException e) {
                return true;
            }
        }

        if (cmd.getName().equalsIgnoreCase("sellmmgui") || cmd.getName().equalsIgnoreCase("smg")) {
            Inventory mirror = Bukkit.createInventory(null,9,"§cSELLMMITEM MENU");
            ItemStack menu1 = new ItemStack(Material.GREEN_STAINED_GLASS);
            ItemStack menu2 = new ItemStack(Material.RED_STAINED_GLASS);
            ItemStack menu3 = new ItemStack(Material.YELLOW_STAINED_GLASS);
            ItemStack menu4 = new ItemStack(Material.BLACK_STAINED_GLASS);
            ItemMeta itemMeta1 = menu1.getItemMeta();
            ItemMeta itemMeta2 = menu2.getItemMeta();
            ItemMeta itemMeta3 = menu3.getItemMeta();
            ItemMeta itemMeta4 = menu4.getItemMeta();
            itemMeta1.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&aSHOPを開く"));
            itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&cSHOPを閉じる"));
            itemMeta3.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&eSHOP注意点"));
            itemMeta4.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&8売却可能アイテム一覧をみる"));
            List<String> lore3 = new ArrayList<String>();
            lore3.add(ChatColor.translateAlternateColorCodes('&',"&dSHOPのインベントリに"));
            lore3.add(ChatColor.translateAlternateColorCodes('&',"&d指定アイテム以外を入れてしまうと"));
            lore3.add(ChatColor.translateAlternateColorCodes('&',"&d一円にもならずアイテムが消えます"));
            lore3.add(ChatColor.translateAlternateColorCodes('&',"&d消えたアイテムに関しては&c補填対象外&dです"));
            itemMeta3.setLore(lore3);
            List<String> lore4 = new ArrayList<String>();
            lore4.add(ChatColor.translateAlternateColorCodes('&',"&d売却可能アイテムは"));
            lore4.add(ChatColor.translateAlternateColorCodes('&',"&3一万円&f,&3五千円&f"));
            lore4.add(ChatColor.translateAlternateColorCodes('&',"&3二千円&f,&3千円&f,&6&l硬貨"));
            lore4.add(ChatColor.translateAlternateColorCodes('&',"&7洞窟の欠片&f,&0&l黒曜石"));
            lore4.add(ChatColor.translateAlternateColorCodes('&',"&f圧縮小麦チケット,圧縮ポテトチケット,圧縮人参チケット"));
            itemMeta4.setLore(lore4);
            menu1.setItemMeta(itemMeta1);
            menu2.setItemMeta(itemMeta2);
            menu3.setItemMeta(itemMeta3);
            menu4.setItemMeta(itemMeta4);
            mirror.setItem(0,menu1);
            mirror.setItem(8,menu2);
            mirror.setItem(5,menu3);
            mirror.setItem(3,menu4);
            p.openInventory(mirror);
        }
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getView().getPlayer();
        ItemStack slot = e.getCurrentItem();
        if (slot == null) return;
        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&cSELLMMITEM MENU"))) {
            if (slot.getType() == Material.GREEN_STAINED_GLASS) {
                if (slot.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aSHOPを開く"))) {
                    Inventory mirror = Bukkit.createInventory(null, 36, "§cSELLMMITEM SHOP");
                    player.openInventory(mirror);
                }
            }else if (slot.getType() == Material.RED_STAINED_GLASS) {
                if (slot.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cSHOPを閉じる"))) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSELLMMSHOP&fを閉じました"));
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Inventory backpack = e.getInventory();
        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&cSELLMMITEM SHOP"))) {
            ItemStack[] contents = backpack.getContents();
            List<String> itemDisplayNameList = new ArrayList<>();
            double totalMoney = 0;
            for (String key : getConfig().getConfigurationSection("mmitem").getKeys(false)) {
                int moneyamount = getConfig().getInt("mmitem." + key + ".sellprice");
                for (int i = 0; i < 36; i++) {
                    ItemStack content = contents[i];
                    if (content == null) {
                        continue;
                    }
                    int amount = content.getAmount();
                    int money = amount * moneyamount;
                    String ItemDisplayName = getConfig().getString("mmitem." + key + ".itemdisplay");
                    if (content.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "" + ItemDisplayName))) {
                        totalMoney += money;
                        itemDisplayNameList.add(ItemDisplayName);
                    }
                }
            }
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', itemDisplayNameList + "&fを売却し" + totalMoney + "&f円獲得しました"));
            getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + e.getPlayer().getName() + " " + totalMoney);
        }
    }

    @EventHandler
    public void onBlockbreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Random random = new Random();
        String world = player.getWorld().getName();
        int num = random.nextInt(30);
        if(world.equals("resource")){
            if (e.getBlock().getType() == Material.OAK_LEAVES) {
                if (num <= 2) {
                    if ((Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta())).getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cトマト採取剣"))) {
                        getServer().dispatchCommand(getServer().getConsoleSender(), "mm i give " + player.getName() + " tomato 2");
                    }
                }
            }
        }
    }
}