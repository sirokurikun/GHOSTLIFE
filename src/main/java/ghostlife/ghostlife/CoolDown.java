package ghostlife.ghostlife;

import java.util.HashMap;

public class CoolDown {
    private static Long coolTime = ghostlife.getInstance().getConfig().getLong("cooltime") * 1000;
    //  プレイヤーごとに分けるため、Mapで保存
    private static HashMap<String, Long> cooldownMap = new HashMap<String, Long>();

    /**
     * クールダウン中か
     *
     * @param uuid uuid(Player.getUniqueId().toString())
     * @return result true=クールダウン中　false=クールダウン終了
     */
    public static Boolean isCoolDown(String uuid) {
        if (!cooldownMap.containsKey(uuid)) return false;
        long now = System.currentTimeMillis();
        long before = cooldownMap.get(uuid);
        return now - before < coolTime;
    }

    /**
     * クールダウンのスタート
     *
     * @param uuid uuid(Player.getUniqueId().toString())
     */
    public static void startCoolDown(String uuid) {
        cooldownMap.remove(uuid);
        cooldownMap.put(uuid, System.currentTimeMillis());
    }
}