package ghostlife.ghostlife;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.Random;

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
    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        String name = p.getDisplayName();
        if ((e.getMessage().contains("@wiki"))) {
            e.setCancelled(true);
            if (!CoolDown.isCoolDown(p.getUniqueId().toString())) {
                String message = getConfig().getString("wikimessage");
                this.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "" + name + message));
                CoolDown.startCoolDown(p.getUniqueId().toString());
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cクールダウン中です。"));
            }
        }
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

        if (cmd.getName().equalsIgnoreCase("addCustomModel")) {
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

        if (cmd.getName().equalsIgnoreCase("sellmmitem")) {
            ItemStack item = p.getInventory().getItemInMainHand();
            int itemAmount = item.getAmount();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.AIR) {
                p.sendMessage(ChatColor.RED + "アイテムを手に持って実行してください！");
                return true;
            }
            if (!item.hasItemMeta()) {
                p.sendMessage(ChatColor.RED + "アイテム名が指定されていないアイテムです。追加できません");
                return true;
            }
            getServer().dispatchCommand(p, "money");
            for (String key : getConfig().getConfigurationSection("mmitem").getKeys(false)) {
                int moneyamount = getConfig().getInt("mmitem." + key + ".sellprice");
                int money = itemAmount * moneyamount;
                String ItemDisplayName = getConfig().getString("mmitem." + key + ".itemdisplay");
                if (itemMeta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "" + ItemDisplayName))) {
                    item.setAmount(0);
                    getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + p.getName() + " " + money);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemDisplayName + "&fを売却し" + money + "&f円獲得しました"));
                }
            }
            getServer().dispatchCommand(p, "money");
        }

        if (cmd.getName().equalsIgnoreCase("backpack")) {
            Inventory mirror = Bukkit.createInventory(null,27,"&dバックパック");
            p.openInventory(mirror);
        }
        return true;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Inventory backpack = e.getInventory();
        if(!e.getView().getTitle().equals("&dバックパック")) return;
        ItemStack[] contents = backpack.getContents();
        for (String key : getConfig().getConfigurationSection("mmitem").getKeys(false)) {
            int moneyamount = getConfig().getInt("mmitem." + key + ".sellprice");
            for (int i = 0; i < 27; i++) {
                ItemStack content = contents[i];
                if(content == null){
                    continue;
                }
                int amount = content.getAmount();
                int money = amount * moneyamount;
                String ItemDisplayName = getConfig().getString("mmitem." + key + ".itemdisplay");
                if (content.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "" + ItemDisplayName))) {
                    getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + e.getPlayer().getName() + " " + money);
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', ItemDisplayName + "&fを売却し" + money + "&f円獲得しました"));
                }
            }

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

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Material material = clickedBlock.getType();
        if (material == Material.OAK_WALL_SIGN || material == Material.OAK_SIGN) {
            Sign sign = (Sign) clickedBlock.getState();
            String line = sign.getLine(0);//引数は[0-3]
            if(line.equals(ChatColor.translateAlternateColorCodes('&', "sellmmitem"))){
                ItemStack item = player.getInventory().getItemInMainHand();
                int itemAmount = item.getAmount();
                ItemMeta itemMeta = item.getItemMeta();
                if (item.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "アイテムを手に持って実行してください！");
                    return;
                }
                if (!item.hasItemMeta()) {
                    player.sendMessage(ChatColor.RED + "アイテム名が指定されていないアイテムです。追加できません");
                    return;
                }
                getServer().dispatchCommand(player, "money");
                for (String key : getConfig().getConfigurationSection("mmitem").getKeys(false)) {
                    int moneyamount = getConfig().getInt("mmitem." + key + ".sellprice");
                    int money = itemAmount * moneyamount;
                    String ItemDisplayName = getConfig().getString("mmitem." + key + ".itemdisplay");
                    if (itemMeta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "" + ItemDisplayName))) {
                        item.setAmount(0);
                        getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + player.getName() + " " + money);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemDisplayName + "&fを売却し" + money + "&f円獲得しました"));
                    }
                }
                getServer().dispatchCommand(player, "money");
            }
        }
    }
}