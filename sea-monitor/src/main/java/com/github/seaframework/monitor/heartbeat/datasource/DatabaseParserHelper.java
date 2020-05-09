
package com.github.seaframework.monitor.heartbeat.datasource;

import com.github.seaframework.core.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class DatabaseParserHelper {
    private Map<String, Database> connections = new LinkedHashMap<String, Database>();

    private String find(String con, String key) {
        int index = con.indexOf(key);
        int start = 0;
        int end = 0;
        if (index > -1) {
            for (int i = index + key.length(); i < con.length(); i++) {
                if (con.charAt(i) == '=') {
                    start = i + 1;
                }
                if (con.charAt(i) == ')') {
                    end = i;
                    break;
                }
            }
        }
        return con.substring(start, end);
    }

    public Database parseDatabase(String connection) {
        Database database = connections.get(String.valueOf(connection));

        if (database == null && StringUtil.isNotEmpty(connection)) {
            try {
                if (connection.contains("jdbc:mysql://")) {
                    String con = connection.split("jdbc:mysql://")[1];
                    con = con.split("\\?")[0];
                    int index = con.indexOf(":");
                    String ip = "";

                    if (index < 0) {
                        ip = con.split("/")[0];
                    } else {
                        ip = con.substring(0, index);
                    }

                    String name = con.substring(con.indexOf("/") + 1);
                    database = new Database(name, ip);

                    connections.put(connection, database);
                } else if (connection.contains("jdbc:oracle")) {
                    if (connection.contains("DESCRIPTION")) {
                        String name = find(connection, "SERVICE_NAME");
                        String ip = find(connection, "HOST");

                        database = new Database(name, ip);
                        connections.put(connection, database);
                    } else if (connection.contains("@//")) {
                        String[] tabs = connection.split("/");
                        String name = tabs[tabs.length - 1];
                        String ip = tabs[tabs.length - 2];
                        int index = ip.indexOf(':');

                        if (index > -1) {
                            ip = ip.substring(0, index);
                        }
                        database = new Database(name, ip);
                        connections.put(connection, database);
                    } else {
                        String[] tabs = connection.split(":");
                        String ip = "Default";

                        for (String str : tabs) {
                            int index = str.indexOf("@");

                            if (index > -1) {
                                ip = str.substring(index + 1).trim();
                            }
                        }
                        String name = tabs[tabs.length - 1];
                        int index = name.indexOf('/');

                        if (index > -1) {
                            name = name.substring(index + 1);
                        }

                        database = new Database(name, ip);

                        connections.put(connection, database);
                    }
                } else {
                    return new Database("default", "default");
                }
            } catch (Exception e) {
                log.error("parse database exception", e);
            }
        }
        return database;
    }

    @Data
    public class Database {
        private String name;
        private String ip;

        Database(String name, String ip) {
            this.name = name;
            this.ip = ip;
        }

        public String toString() {
            return name + '_' + ip;
        }
    }
}
