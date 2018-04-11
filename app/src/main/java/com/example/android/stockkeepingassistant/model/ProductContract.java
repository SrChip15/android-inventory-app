package com.example.android.stockkeepingassistant.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {
	// To prevent anyone from instantiating an object of this class,
	// make a private constructor
	private ProductContract() {
	}

	/**
	 * The "Content authority" is a name for the entire content provider, similar to the
	 * relationship between a domain name and its website.  A convenient string to use for the
	 * content authority is the package name for the app, which is guaranteed to be unique on the
	 * device.
	 */
	public static final String CONTENT_AUTHORITY = "com.example.android.stockkeepingassistant";

	/**
	 * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
	 * the content provider.
	 */
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	/** Possible path (appended to base content URI for possible URI's) */
	public static final String PATH_PRODUCTS = "products";

	/** Inner class that defines the table contents */
	public static class ProductEntry implements BaseColumns {
		/** The content URI to access the product data in the provider */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

		/** The MIME type of the {@link #CONTENT_URI} for a list of products. */
		public static final String CONTENT_LIST_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		/** The MIME type of the {@link #CONTENT_URI} for a single pet. */
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		public static final String TABLE_NAME = "products";
		public static final String _ID = BaseColumns._ID;
		public static final String UUID = "uuid";
		public static final String COLUMN_PRODUCT_TITLE = "title";
		public static final String COLUMN_PRODUCT_PRICE = "price";
		public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
		public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
		public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
	}

}
