package com.termites.tools.javabean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LF on 16/10/21.
 */

public class CustomBean implements Serializable {
    private int customId;
    private String customCode;
    private List<Admins> admins;

    public String getCustomCode() {
        return customCode;
    }

    public int getCustomId() {
        return customId;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public void setCustomId(int customId) {
        this.customId = customId;
    }

    public List<Admins> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Admins> admins) {
        this.admins = admins;
    }

    public class Admins {
        private String name;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
