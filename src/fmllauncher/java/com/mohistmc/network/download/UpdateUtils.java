package com.mohistmc.network.download;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mohistmc.MohistMCStart;
import com.mohistmc.util.JarTool;
import com.mohistmc.util.i18n.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mohistmc.config.MohistConfigUtil.bMohist;
import static com.mohistmc.network.download.NetworkUtil.getConn;
import static com.mohistmc.network.download.NetworkUtil.getInput;

public class UpdateUtils {

    private static int percentage = 0;

    public static void versionCheck() {
        System.out.println(i18n.get("update.check"));
        System.out.println(i18n.get("update.stopcheck"));

        try {
            JsonElement root = new JsonParser().parse(new InputStreamReader(getInput("https://api.github.com/repos/MinecraftMist/Mist/actions/runs?status=success&branch=1.16.5")));

            String jar_sha = MohistMCStart.getVersion();
            String build_number = "1.16.5-" + root.getAsJsonObject().get("total_count").toString();

            if (jar_sha.equals(build_number))
                System.out.println(i18n.get("update.latest", "1.0", jar_sha, build_number));
            else {
                System.out.println(i18n.get("update.detect", build_number, jar_sha));
                if (bMohist("check_update_auto_download", "false")) {
                    downloadFile("https://github.com/MinecraftMist/Mist/releases/download/" + build_number + "/mist-" + build_number + "-server.jar", JarTool.getFile());
                    restartServer(new ArrayList<>(Arrays.asList("java", "-jar", JarTool.getJarName())), true);
                }
            }
        } catch (Throwable e) {
            System.out.println(i18n.get("check.update.noci"));
        }
    }

    public static void downloadFile(String URL, File f) throws Exception {
        URLConnection conn = getConn(URL);
        System.out.println(i18n.get("download.file", f.getName(), getSize(conn.getContentLength())));
        ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
        FileChannel fc = FileChannel.open(f.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        int fS = conn.getContentLength();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (rbc.isOpen()) {
                    if (percentage != Math.round((float) f.length() / fS * 100) && percentage < 100)
                        System.out.println(i18n.get("file.download.percentage", f.getName(), percentage));
                    percentage = Math.round((float) f.length() / fS * 100);
                } else t.cancel();
            }
        }, 3000, 1000);
        fc.transferFrom(rbc, 0, Long.MAX_VALUE);
        fc.close();
        rbc.close();
        System.out.println(i18n.get("download.file.ok", f.getName()));
        percentage = 0;
    }

    public static void restartServer(ArrayList<String> cmd, boolean shutdown) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();
        process.waitFor();
        Thread.sleep(2000);
        if (shutdown) System.exit(0);
    }

    public static String getSize(long size) {
        return (size >= 1048576L) ? (float) size / 1048576.0F + "MB" : ((size >= 1024) ? (float) size / 1024.0F + " KB" : size + " B");
    }

    public static long getSizeOfDirectory(File path) throws IOException {
        return Files.walk(path.toPath()).parallel()
                .map(Path::toFile)
                .filter(File::isFile)
                .mapToLong(File::length)
                .sum();
    }
}
