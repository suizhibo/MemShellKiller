package xxxx.agent;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    public static List<String> witheTransformerNames = Arrays.asList("org.xxxx.core.trasnformer.ClassDumpTransformer", "org.xxxx.core.trasnformer.GenieTransformer",
            "org.xxxx.core.trasnformer.KillerTransformer", "org.xxxx.core.trasnformer.ScanTransformer", "org.xxxx.core.trasnformer.ProtectedTransformer");

    public static String scanResultTemplate = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Tomcat MemShell Killer</title>\n" +
            "<style>\n" +
            "    td {\n" +
            "    text-align:center;\n" +
            "    }\n" +
            "</style></head>\n" +
            "<body>\n" +
            "<div style=\"color:#808080;width:100%\">\n" +
            "    <h1 style=\"text-align:center;\">MemShell Killer</h1>\n" +
            "</div>\n" +
            "<div style=\"margin-top:30px\">\n" +
            "    <h3 style=\"text-align:center;\">Filter</h3>\n" +
            "<table>\n" +
            "    <table border=\"1\">\n" +
           " <thead>\n" +
            "            <tr>\n" +
            "                <th style=\"width: 5%;\">ID</th>\n" +
            "                <th style=\"width: 10%;\">Name</th>\n" +
            "                <th style=\"width: 10%;\">Pattern</th>\n" +
            "                <th style=\"width: 20%;\">Class</th>\n" +
            "                <th style=\"width: 20%;\">ClassLoader</th>\n" +
            "                <th style=\"width: 25%;\">File Path</th>\n" +
            "                <th style=\"width: 5%;\">Dump</th>\n" +
            "                <th style=\"width: 5%;\">Kill</th>\n" +
            "            </tr>\n" +
            "            </thead>"+
            "        {filter}\n" +
            "    </table>\n" +
            "</table>\n" +
            "</div>\n" +
            "\n" +
            "<div style=\"margin-top:30px\">\n" +
            "    <h3 style=\"text-align:center;\">Listener</h3>\n" +
            "    <table>\n" +
            "        <table border=\"1\">\n" +
            "            <tr>\n" +
            "                <th>ID</th>\n" +
            "                <th>Name</th>\n" +
            "                <th>Pattern</th>\n" +
            "                <th>Class</th>\n" +
            "                <th>ClassLoader</th>\n" +
            "                <th>File Path</th>\n" +
            "                <th>Dump</th>\n" +
            "                <th>Kill</th>\n" +
            "            </tr>\n" +
            "            {listener}\n" +
            "        </table>\n" +
            "    </table>\n" +
            "</div>\n" +
            "\n" +
            "<div style=\"margin-top:30px\">\n" +
            "    <h3 style=\"text-align:center;\">Servlet</h3>\n" +
            "    <table>\n" +
            "        <table border=\"1\">\n" +
            " <thead>\n" +
            "            <tr>\n" +
            "                <th style=\"width: 5%;\">ID</th>\n" +
            "                <th style=\"width: 10%;\">Name</th>\n" +
            "                <th style=\"width: 10%;\">Pattern</th>\n" +
            "                <th style=\"width: 20%;\">Class</th>\n" +
            "                <th style=\"width: 20%;\">ClassLoader</th>\n" +
            "                <th style=\"width: 25%;\">File Path</th>\n" +
            "                <th style=\"width: 5%;\">Dump</th>\n" +
            "                <th style=\"width: 5%;\">Kill</th>\n" +
            "            </tr>\n" +
            "            </thead>"+
            "            {servlet}\n" +
            "        </table>\n" +
            "    </table>\n" +
            "</div>\n" +
            "\n" +
            "<div style=\"margin-top:30px\">\n" +
            "    <h3 style=\"text-align:center;\">Transform & BlackList</h3>\n" +
            "    <table>\n" +
            "        <table border=\"1\">\n" +
            " <thead>\n" +
            "            <tr>\n" +
            "                <th style=\"width: 5%;\">ID</th>\n" +
            "                <th style=\"width: 10%;\">Name</th>\n" +
            "                <th style=\"width: 10%;\">Pattern</th>\n" +
            "                <th style=\"width: 20%;\">Class</th>\n" +
            "                <th style=\"width: 20%;\">ClassLoader</th>\n" +
            "                <th style=\"width: 25%;\">File Path</th>\n" +
            "                <th style=\"width: 5%;\">Dump</th>\n" +
            "                <th style=\"width: 5%;\">Kill</th>\n" +
            "            </tr>\n" +
            "            </thead>"+
            "            {transformer}\n" +
            "        </table>\n" +
            "    </table>\n" +
            "</div>\n" +
            "<footer>\n" +
            "  <p>Created by: BJSec</p>\n" +
            "</footer>" +
            "</body>\n" +
            "</html>";


    public static Properties properties;
    public static void loadConfig(String configPath) throws Exception {
        try {
            properties = new Properties();
            InputStream in = new FileInputStream(configPath);
            properties.load(in);
        }catch (Exception e){
            throw e;
        }
    }
}
