package com.example.android.stockkeepingassistant.data;

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
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	/**
	 * Possible path (appended to base content URI for possible URI's)
	 */
	public static final String PATH_PRODUCTS = "products";

	/**
	 * Inner class that defines the table contents
	 */
	public static class ProductEntry implements BaseColumns {
		/** The content URI to access the product data in the provider */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

		/**
		 * The MIME type of the {@link #CONTENT_URI} for a list of products.
		 */
		public static final String CONTENT_LIST_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		/**
		 * The MIME type of the {@link #CONTENT_URI} for a single pet.
		 */
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		public static final String TABLE_NAME = "products";
		public static final String _ID = BaseColumns._ID;
		public static final String COLUMN_PRODUCT_IMAGE = "image";
		public static final String COLUMN_PRODUCT_DESC = "description";
		public static final String COLUMN_PRODUCT_PRICE = "price";
		public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
		public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
		public static final String COLUMN_PRODUCT_SUPPLIER_CONTACT = "supplier_email";

		public static final String SUPPLIER_1 = "supplier_1@gmail.com";
		public static final String SUPPLIER_2 = "supplier_2@gmail.com";
		public static final String SUPPLIER_3 = "supplier_3@gmail.com";
		public static final String SUPPLIER_4 = "supplier_4@gmail.com";
		public static final String SUPPLIER_5 = "supplier_5@gmail.com";
		public static final String SUPPLIER_6 = "supplier_6@gmail.com";
		public static final String SUPPLIER_7 = "supplier_7@gmail.com";
		public static final String SUPPLIER_8 = "supplier_8@gmail.com";
		public static final String SUPPLIER_9 = "supplier_9@gmail.com";
		public static final String SUPPLIER_10 = "supplier_10@gmail.com";
	}

}
