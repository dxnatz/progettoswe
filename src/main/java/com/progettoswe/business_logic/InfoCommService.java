package com.progettoswe.business_logic;

import com.progettoswe.ORM.InfoCommDAO;

import java.util.ArrayList;

public class InfoCommService {

    public static ArrayList<String> getCommentiOpera() {
        return InfoCommDAO.getCommentiOpera();
    }
}
