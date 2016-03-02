package com.hieptran.devicesmanager.utils.profile;

import com.hieptran.devicesmanager.utils.Const;

/**
 * Created by hieptran on 27/02/2016.
 */
public class Profile implements Const {
    private String name;
    private String description;
    private boolean isChecked;

    public Profile(String name, String description, boolean isChecked) {
        this.name = name;
        this.description = description;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
