package com.mohistmc;

import com.mohistmc.config.MohistConfigUtil;
import com.mohistmc.libraries.CustomLibraries;
import com.mohistmc.libraries.DefaultLibraries;
import com.mohistmc.network.download.UpdateUtils;
import com.mohistmc.util.*;
import com.mohistmc.util.i18n.i18n;

import java.util.Scanner;

import static com.mohistmc.util.EulaUtil.hasAcceptedEULA;
import static com.mohistmc.util.EulaUtil.writeInfos;
import static com.mohistmc.util.InstallUtils.startInstallation;
import static com.mohistmc.util.PluginsModsDelete.checkPlugins;

public class MohistMCStart {

    public static String getVersion() {
        return (MohistMCStart.class.getPackage().getImplementationVersion() != null) ? MohistMCStart.class.getPackage().getImplementationVersion() : "unknown";
    }

    public static void main() throws Exception {
        MohistConfigUtil.copyMohistConfig();
        CustomFlagsHandler.handleCustomArgs();

        if (MohistConfigUtil.bMohist("show_logo", "true")) {
            System.out.println("\n\n __    __   __   ______   ______ ");
            System.out.println("/\\ \"-./  \\ /\\ \\ /\\  ___\\ /\\__  _\\");
            System.out.println("\\ \\ \\-./\\ \\\\ \\ \\\\ \\___  \\\\/_/\\ \\/");
            System.out.println(" \\ \\_\\ \\ \\_\\\\ \\_\\\\/\\_____\\  \\ \\_\\");
            System.out.println("  \\/_/  \\/_/ \\/_/ \\/_____/   \\/_/");
            System.out.println("\n\n                                      " + i18n.get("mohist.launch.welcomemessage"));
        }
        if (MohistConfigUtil.bMohist("check_libraries", "true")) {
            DefaultLibraries.run();
            startInstallation();
        }
        CustomLibraries.loadCustomLibs();
        new JarLoader().loadJar(InstallUtils.extra);

        // make sure gson use this EnumTypeAdapter
        Class.forName("com.google.gson.internal.bind.TypeAdapters$EnumTypeAdapter").getClassLoader();

        if (MohistConfigUtil.bMohist("check_update", "true")) UpdateUtils.versionCheck();
        if (!hasAcceptedEULA()) {
            System.out.println(i18n.get("eula"));
            while (!"true".equals(new Scanner(System.in).next())) ;
            writeInfos();
        }
        if (!MohistConfigUtil.bMohist("disable_plugins_blacklist", "false"))
			checkPlugins(AutoDeletePlugins.LIST, PluginsModsDelete.PLUGIN);

        if (!MohistConfigUtil.bMohist("disable_mods_blacklist", "false"))
			checkPlugins(AutoDeleteMods.LIST, PluginsModsDelete.MOD);
    }
}
