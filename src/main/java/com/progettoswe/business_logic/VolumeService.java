package com.progettoswe.business_logic;

import com.progettoswe.model.Volume;

import java.sql.SQLException;

public class VolumeService {

    public static boolean addVolume(Volume v) throws SQLException {
        if(VolumeService.addVolume(v)){
            return true;
        }
        return false;
    }
}
