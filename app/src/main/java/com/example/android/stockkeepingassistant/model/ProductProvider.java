package com.example.android.stockkeepingassistant.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.model.ProductContract.ProductEntry;

import java.util.Objects;

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String TAG = ProductProvider.class.getSimpleName();

    /** Database helper that will provide us access to the database */
    private ProductDbHelper mDbHelper;

    /** Whole table query situation */
    private static final int PRODUCTS = 100;

    /** Row query situation */
    private static final int PRODUCT_ID = 101;

    /** Utility class to aid in matching URIs */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private Context context;

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.products/products" will map to the
        // integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
        // of the products table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        // The content URI of the form "content://com.example.android.products/products/#" will map to the
        // integer code {@link #PRODUCT_ID}. This URI is used to provide access to ONE single row
        // of the products table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.products/products/3" matches, but
        // "content://com.example.android.products/products" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        context = Objects.requireNonNull(getContext());
        mDbHelper = new ProductDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        // Get read access to database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Declare cursor
        Cursor cursor;

        // Get situation ID
        int match = sUriMatcher.match(uri);

        // Cases
        switch (match) {
            case PRODUCTS:
                cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_ID:
                selection = ProductEntry.UUID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Error: Unknown URI " + uri);
        }

        // Set notification URI on the cursor,
        // so we know what content URI the cursor was created for.
        // If the data at this URI changes, then we need to update the cursor.
        cursor.setNotificationUri(context.getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                String info = Objects.requireNonNull(getContext()).getString(
                        R.string.provider_type_mismatch_part1) +
                        uri +
                        getContext().getString(R.string.provider_type_mismatch_part2) +
                        match;

                throw new IllegalStateException(info);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(context.getString(R.string.unsupported_insertion) + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get write-able database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Get match ID using uri matcher to determine the type of delete (single vs multiple)
        final int match = sUriMatcher.match(uri);

        // Declare variable to hold the number of records deleted information
        int numRecordsDeleted;

        // Filter on match ID
        switch (match) {
            case PRODUCTS:
                // Delete records matching criteria
                numRecordsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (numRecordsDeleted != 0) {
                    // One or more rows were updated, so broadcast to all listeners
                    // that the data has changed for the product content URI
                    context.getContentResolver().notifyChange(uri, null);
                }

                // Return number of records deleted
                return numRecordsDeleted;
            case PRODUCT_ID:
                selection = ProductEntry.UUID + "=?";

                // Parse the row ID from the URI.
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Delete the specific row
                numRecordsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

                if (numRecordsDeleted != 0) {
                    // One or more rows were updated, so broadcast to all listeners
                    // that the data has changed for the product content URI
                    context.getContentResolver().notifyChange(uri, null);
                }

                // Return number of rows deleted
                return numRecordsDeleted;
            default:
                throw new IllegalArgumentException(
                        context.getString(R.string.delete_product_failure) + uri
                );
        }
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get the match ID using the uri matcher
        final int match = sUriMatcher.match(uri);

        // Filter on match ID and perform action
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry.UUID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(context.getString(R.string.update_product_failure) + uri);
        }
    }

    @Nullable
    private Uri insertProduct(Uri uri, @Nullable ContentValues contentValues) {
        if (contentValues == null) {
            return null;
        }
        // Get values from contentValues to validate.
        // Product image is not a required field. So, it is not validated here.
        String uuidString = contentValues.getAsString(ProductEntry.UUID);
        String title = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_TITLE);
        Integer quantity = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        String price = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
        String supplierName = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
        String supplierEmail = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);

        if (uuidString == null || uuidString.isEmpty()) {
            throw new IllegalArgumentException("Product class did not generate ID!");
        }

        // Check whether product description is provided
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_name));
        }

        // Check whether quantity of product is provided
        if (quantity == null || quantity < 0) {
            // Negative number entered as quantity
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_quantity));
        }

        // Check whether price information is provided
        if (price == null || price.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_price));
        }

        // Check whether supplier information is provided
        if (supplierName == null || supplierName.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_supplier));
        }

        // Check whether supplier contact information is provided
        if (supplierEmail == null || supplierEmail.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_supplier_contact));
        }

        // Get write-able database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(TAG, context.getString(R.string.insert_failure) + uri);
            return null;
        }

        // Broadcast to all listeners that the data has changed for the product content URI
        // Android framework is setup in a such a way that the cursor adapter associated with
        // the cursor is notified of the changes when the content observer is set to null.
        // This triggers a reload of the ListView fronting the adapter.
        context.getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri,
                              @Nullable ContentValues values,
                              @Nullable String selection,
                              @Nullable String[] selectionArgs) {
        if (values != null && values.size() != 0) {
            // Check for valid product description
            if (values.containsKey(ProductEntry.COLUMN_PRODUCT_TITLE)) {
                String title = values.getAsString(ProductEntry.COLUMN_PRODUCT_TITLE);
                if (title == null || title.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_desc));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
                // Check for valid quantity
                Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                if (quantity == null || quantity < 0) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_quantity));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
                // Check for valid price information
                String price = values.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
                if (price == null || price.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_price));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
                // Check for valid supplier information
                String supplier = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
                if (supplier == null || supplier.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_supplier));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
                // Check for valid supplier contact information
                String contact = values.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);
                if (contact == null || contact.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_supplier_contact));
                }
            }
        } else {
            // When nothing is passed via the ContentValues to be updated
            // Do not perform any database operation
            return 0;
        }

        // Get write-able database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int numRowsAffected = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (numRowsAffected != 0) {
            // One or more rows were updated, so broadcast to all listeners
            // that the data has changed for the product content URI
            context.getContentResolver().notifyChange(uri, null);
        }

        // Return rows affected
        return numRowsAffected;
    }
}
