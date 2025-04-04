package com.progettoswe.business_logic;

import com.progettoswe.model.Opera;
import com.progettoswe.ORM.OperaDAO;

import java.sql.SQLException;
import java.util.List;

public class OperaService {

    public static Opera getOperaById(int id) throws SQLException {
        return OperaDAO.getOperaById(id);
    }

    public static List<Opera> getAllOpere() throws SQLException {
        return OperaDAO.getAllOpere();
    }

    public static int addOpera(Opera opera) throws SQLException {
        return OperaDAO.insertOpera(opera);
    }

    public static boolean updateOpera(Opera opera) throws SQLException {
        return OperaDAO.updateOpera(opera);
    }

    public static boolean deleteOpera(int id) throws SQLException {
        return OperaDAO.deleteOpera(id);
    }

    public static List<Opera> searchOpere(String searchTerm) throws SQLException {
        return OperaDAO.searchOpere(searchTerm);
    }

}