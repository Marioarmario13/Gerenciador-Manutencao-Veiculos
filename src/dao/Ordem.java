package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Monta o ORDER BY com até dois critérios, aceitando apenas colunas permitidas (evita SQL injection). */
class Ordem {
    static String montar(Map<String, String> colunasPermitidas, String criterio1, String criterio2) {
        List<String> colunas = new ArrayList<>();
        for (String criterio : new String[]{criterio1, criterio2}) {
            String coluna = colunasPermitidas.get(criterio);
            if (coluna != null && !colunas.contains(coluna)) colunas.add(coluna);
        }
        return colunas.isEmpty() ? "" : " ORDER BY " + String.join(", ", colunas);
    }
}
