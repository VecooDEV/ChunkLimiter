package com.vecoo.chunklimiter.util;

import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public class PermissionNodes {
    public static List<PermissionNode<?>> permissionList = new ArrayList<>();

    public static PermissionNode<Boolean> CHUNKLIMITER_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.cl",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> CHUNKLIMITER_RELOAD_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.cl.reload",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> CHUNKLIMITER_ATTRIBUTE_IGNORE = new PermissionNode<>(
            "chunklimiter",
            "attribute.ignore",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);
}
