package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Métodos auxiliares de validação e formatação. */
public class Util {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static int parseInt(String s, String campo, int min, int max) {
        if (vazio(s)) throw new AppException(campo + " é obrigatório.");
        try {
            int v = Integer.parseInt(s.trim());
            if (v < min || v > max) {
                throw new AppException(campo + " deve estar entre " + min + " e " + max + ".");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new AppException(campo + " deve ser um número inteiro.");
        }
    }

    /** Aceita "150", "150.50" ou "1.250,50". */
    public static BigDecimal parseDecimal(String s, String campo) {
        if (vazio(s)) throw new AppException(campo + " é obrigatório.");
        String t = s.trim();
        if (t.contains(",")) t = t.replace(".", "").replace(",", ".");
        try {
            BigDecimal v = new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
            if (v.signum() < 0) throw new AppException(campo + " não pode ser negativo.");
            if (v.compareTo(new BigDecimal("99999999.99")) > 0) throw new AppException(campo + " muito alto.");
            return v;
        } catch (NumberFormatException e) {
            throw new AppException(campo + " inválido. Exemplo: 150,00");
        }
    }

    public static Date parseData(String s, String campo) {
        if (vazio(s)) throw new AppException(campo + " é obrigatória.");
        try {
            return Date.valueOf(LocalDate.parse(s.trim(), FMT));
        } catch (DateTimeParseException e) {
            throw new AppException(campo + " inválida. Use o formato dd/mm/aaaa.");
        }
    }

    public static String formatData(Date d) {
        return d == null ? "" : d.toLocalDate().format(FMT);
    }

    public static String hoje() {
        return LocalDate.now().format(FMT);
    }

    public static String moeda(BigDecimal v) {
        if (v == null) v = BigDecimal.ZERO;
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %,.2f", v);
    }
}
