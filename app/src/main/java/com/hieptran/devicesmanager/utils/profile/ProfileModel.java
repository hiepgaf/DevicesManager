package com.hieptran.devicesmanager.utils.profile;

import com.hieptran.devicesmanager.utils.Const;

/**
 * Created by hieptran on 07/03/2016.
 */
public class ProfileModel implements Const {
    private String name;
    private String description;
    private boolean enabled = false;
    private int priority;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
