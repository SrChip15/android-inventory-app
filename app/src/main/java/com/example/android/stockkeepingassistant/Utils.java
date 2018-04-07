package com.example.android.stockkeepingassistant;

import java.text.DecimalFormat;

public final class Utils {
    public static String currencyFormatter(float productPrice) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        return df.format(productPrice);
    }
}
