package ca.laurentian.concluder.refactorState;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class SystemConfiguration {
    public static final String GRAPH_FILE = "graph";
    public static final String GRAPH_NODES = "graph.nodes";
    public static final String GRAPH_EDGES = "graph.edges";

    public static final String TREE_FILE = "tree";
    public static final String TREE_NODES = "tree.nodes";
    public static final String TREE_EDGES = "tree.edges";

    private static Locale locale;
    private static DecimalFormat df;
    private static DecimalFormat df_default;

    private SystemConfiguration() {
    }

    public static void setSystemLocale(Locale l) {
        locale = l;
    }

    public static void setSystemDefaultNumberFormat() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.CANADA);
        otherSymbols.setDecimalSeparator('.');
        df_default = new DecimalFormat("#0.0", otherSymbols);
    }

    public static void setSystemNumberFormat() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(locale);
        df = new DecimalFormat("#0.0", otherSymbols);
    }

    public static String formatNumber(double d) {
        return df.format(d);
    }

    public static double unformatNumberString(String n) throws Exception {
        n = n.replace("" + df.getDecimalFormatSymbols().getDecimalSeparator(), "" + df_default.getDecimalFormatSymbols().getDecimalSeparator());
        //quick fix against zero, should return previous to edit value
        if (Double.parseDouble(n) == 0)
            return 1;
        else
            return Double.parseDouble(n);
    }
}
