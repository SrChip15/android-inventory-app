package com.example.android.stockkeepingassistant.model;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.Nullable;

import com.example.android.stockkeepingassistant.model.ProductContract.ProductEntry;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductCursorWrapper extends CursorWrapper {

    ProductCursorWrapper(@Nullable Cursor cursor) {
        super(cursor);
    }

    public Product getProduct() {
        String uuidString = getString(getColumnIndex(ProductEntry.UUID));
        String productTitle = getString(getColumnIndex(ProductEntry.COLUMN_PRODUCT_TITLE));
        int quantity = getInt(getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        BigDecimal price = new BigDecimal(getString(getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE)));
        String supplierName = getString(getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME));
        String supplierEmail = getString(getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL));

        Product product = new Product(UUID.fromString(uuidString));
        product.setTitle(productTitle);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setSupplierName(supplierName);
        product.setSupplierEmail(supplierEmail);

        return product;
    }
}
