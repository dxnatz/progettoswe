package com.progettoswe.business_logic;

import com.progettoswe.ORM.EdizioneDAO;
import com.progettoswe.model.Edizione;

public class EdizioneService {

    public static Edizione getEdizione(String titolo, int edizione) {
        return EdizioneDAO.getEdizione(titolo, edizione);
    }
}
