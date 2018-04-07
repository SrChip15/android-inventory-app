package com.example.android.stockkeepingassistant.view.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.Utils;
import com.example.android.stockkeepingassistant.model.ProductContract.ProductEntry;

public class ProductCursorAdapter
		extends CursorAdapter
		implements View.OnClickListener {

	private LayoutInflater mInflater;

	public ProductCursorAdapter(Context context, Cursor c) {
		super(context, c, 0 /* flags */);

		mInflater = LayoutInflater.from(context);
	}

	/**
	 * Defines a class that hold resource IDs of each item layout
	 * row to prevent having to look them up each time data is
	 * bound to a row.
	 */
	private class ViewHolder {
		ImageView productImageView;
		TextView productDescTextView;
		TextView productQuantityTextView;
		TextView productPriceTextView;
		Button sellButtonView;
	}

	@Override
	public View newView(Context context,
	                    Cursor cursor,
	                    ViewGroup viewGroup) {
		/* Inflates the item layout. Stores resource IDs in a
             * in a ViewHolder class to prevent having to look
             * them up each time bindView() is called.
             */
		final View itemView = mInflater.inflate(
				R.layout.list_item,
				viewGroup,
				false
		);

		// Holder to cache view lookups
		final ViewHolder holder = new ViewHolder();
		// Cache handle on views
		holder.productImageView = itemView.findViewById(R.id.product_image_view);
		holder.productDescTextView = itemView.findViewById(R.id.list_item_product_desc);
		holder.productQuantityTextView = itemView.findViewById(R.id.list_item_product_quantity);
		holder.productPriceTextView = itemView.findViewById(R.id.list_item_product_price);
		holder.sellButtonView = itemView.findViewById(R.id.list_item_sell_button);

		// Set cache on the view before the view gets passed to the bindView() method callback
		itemView.setTag(holder);

		// Return the constructed view
		return itemView;
	}

	@Override
	public void bindView(final View view, Context context, final Cursor cursor) {
		// Get the cache from the view
		final ViewHolder holder = (ViewHolder) view.getTag();

		// Find the columns of product attributes that we're interested in
		int rowIdColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
		int descColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DESC);
		int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
		int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

		// Read the product attributes from the Cursor for the current product
		int rowId = cursor.getInt(rowIdColumnIndex);
		final String productDesc = cursor.getString(descColumnIndex);
		final int quantity = cursor.getInt(quantityColumnIndex);
		final float price = cursor.getFloat(priceColumnIndex);

		// Setup listener on sell button
		holder.sellButtonView.setOnClickListener(this);

		// Tag list item information
		ItemValues item = new ItemValues(rowId,quantity);
		holder.sellButtonView.setTag(item);

		// Update Views with the attributes of the current product
		holder.productDescTextView.setText(productDesc);
		holder.productQuantityTextView.setText(String.valueOf(quantity));
        holder.productPriceTextView.setText(context.getString(R.string.list_item_price_label));

        String priceStr = Utils.currencyFormatter(price);
        holder.productPriceTextView.append(priceStr);
	}

	@Override
	public void onClick(View view) {
		// Get the clicked item's information as a data object
		ItemValues modItem = (ItemValues) view.getTag();

		// Revise quantity only when there is stock of the current item
		if (modItem.getQuantity() > 0) {
			// Stock available
			// Revise quantity
			int revisedQuantity = modItem.getQuantity() - 1;

			// Create content values with updated quantity value
			ContentValues updatedValues = new ContentValues();
			updatedValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, revisedQuantity);

			// Construct item URI
			Uri itemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, modItem.getRowId());

			// Update quantity in database.
			// As the notification URI is set in the content provider, the update query will
			// automatically trigger view refresh
			view.getContext().getContentResolver().update(itemUri, updatedValues, null, null);

		} else {
			// Stock unavailable
			// Display stock status to user
			Toast.makeText(
					view.getContext(),
					view.getContext().getString(R.string.list_item_qty_not_sufficient),
					Toast.LENGTH_SHORT
			)
					.show();
		}
	}

	/**
	 * Inner class to store row ID and quantity information of the clicked item when the
	 * sell button is clicked
	 */
	static private class ItemValues {
		int rowId;              /*  from the database */
		int quantity;           /*  from the database */

		ItemValues(int rowId, int quantity) {
			this.rowId = rowId;
			this.quantity = quantity;
		}

		/** Return the row Id of the product */
		private int getRowId() {
			return rowId;
		}

		/** Return the quantity of the product */
		private int getQuantity() {
			return quantity;
		}
	}
}
