package com.example.android.stockkeepingassistant;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.stockkeepingassistant.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {
	public ProductCursorAdapter(Context context, Cursor c) {
		super(context, c, 0 /* flags */);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Inflate the UI for the list item
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Find individual views that we want to modify in the list item layout
		TextView productDescTextView = (TextView) view.findViewById(R.id.list_item_product_desc);
		TextView productQuantityTextView = (TextView) view.findViewById(R.id.list_item_product_quantity);
		TextView productPriceTextView = (TextView) view.findViewById(R.id.list_item_product_price);

		// Find the columns of product attributes that we're interested in
		int descColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DESC);
		int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
		int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

		// Read the product attributes from the Cursor for the current product
		String productDesc = cursor.getString(descColumnIndex);
		String quantityStr = String.valueOf(cursor.getInt(quantityColumnIndex));
		String priceStr = String.valueOf(cursor.getInt(priceColumnIndex));

		// Update the TextViews with the attributes for the current product
		productDescTextView.setText(productDesc);
		productQuantityTextView.setText(quantityStr);
		productPriceTextView.setText(priceStr);
	}
}
