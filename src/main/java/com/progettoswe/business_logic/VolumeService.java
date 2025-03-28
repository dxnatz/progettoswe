package com.progettoswe.business_logic;

import com.progettoswe.model.Volume;
import com.progettoswe.ORM.VolumeDAO;

import java.sql.SQLException;
import java.util.List;

public class VolumeService {

    public static Volume getVolume(int idVolume) {
        return VolumeDAO.getVolumeById(idVolume);
    }

    public static boolean aggiungiVolume(Volume volume) {
        return VolumeDAO.insertVolume(volume);
    }

    public static boolean modificaVolume(Volume volume) {
        return VolumeDAO.updateVolume(volume);
    }

    public static boolean rimuoviVolume(int idVolume) {
        return VolumeDAO.deleteVolume(idVolume);
    }

    public static boolean cambiaStatoVolume(int idVolume, String nuovoStato) {
        return VolumeDAO.updateStatoVolume(idVolume, nuovoStato);
    }

    public static boolean verificaDisponibilita(int idVolume) {
        Volume volume = getVolume(idVolume);
        return volume != null &&
                volume.getStato().equals("disponibile") &&
                !VolumeDAO.isVolumeInPrestito(idVolume);
    }

    public static List<Volume> getAllVolumi() throws SQLException {
        return VolumeDAO.getAllVolumi();
    }

    public static List<Volume> searchVolumi(String searchTerm) throws SQLException {
        return VolumeDAO.searchVolumi(searchTerm);
    }
}