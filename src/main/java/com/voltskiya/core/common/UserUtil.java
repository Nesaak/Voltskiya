package com.voltskiya.core.common;

import com.voltskiya.core.Voltskiya;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class UserUtil {

    private static LuckPerms luckPerms = Voltskiya.get().getLuckPerms();

    public static String getDisplayName(OfflinePlayer player) {
        User user = getUser(player.getUniqueId());
        if (user == null) return (((player.isOp()) ? DARK_RED + "[" + RED + "Admin" + DARK_RED + "] " + RED : "") + player.getName());
        CachedMetaData data = user.getCachedData().metaData().get(QueryOptions.nonContextual());
        String prefix = data.getPrefix();
        if (prefix == null) prefix = "";
        if (data.getPrefix() == null) ;
        return translateAlternateColorCodes('&', prefix + " " + player.getName());
    }

    public static User getUser(UUID uuid) {
        if (luckPerms == null) return null;
        return luckPerms.getUserManager().getUser(uuid);
    }
}
