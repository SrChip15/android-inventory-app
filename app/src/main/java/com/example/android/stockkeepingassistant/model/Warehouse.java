package com.example.android.stockkeepingassistant.model;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.android.stockkeepingassistant.R;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    /* Class variables */
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static Warehouse warehouse;
    private Map<String, String> supplierToEmailMap;

    private Warehouse(Context context) {
        this.context = context;
        makeEmailDirectory();
    }

    public static Warehouse getInstance(Context context) {
        if (warehouse == null) {
            warehouse = new Warehouse(context.getApplicationContext());
        }

        return warehouse;
    }

    public String resolveEmail(String supplierName) {
        return supplierToEmailMap.get(supplierName);
    }

    public String resolveName(String supplierEmail) {
        for (Map.Entry<String, String> entry : supplierToEmailMap.entrySet()) {
            if (entry.getValue().equals(supplierEmail)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void makeEmailDirectory() {
        supplierToEmailMap = new HashMap<>(10);
        supplierToEmailMap.put(context.getString(R.string.supplier_1), "staples@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_2), "depot@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_3), "barnes.noble@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_4), "target@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_5), "it.books@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_6), "paper.source@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_7), "maido@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_8), "best.buy@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_9), "office.max@gmail.com");
        supplierToEmailMap.put(context.getString(R.string.supplier_10), "booksmith@gmail.com");
    }
}
