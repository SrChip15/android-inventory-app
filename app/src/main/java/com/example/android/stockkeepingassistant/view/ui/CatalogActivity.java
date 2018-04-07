package com.example.android.stockkeepingassistant.view.ui;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.model.ProductContract.ProductEntry;
import com.example.android.stockkeepingassistant.view.adapter.ProductCursorAdapter;

public class CatalogActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor> {

	private ProductCursorAdapter mCursorAdapter;

	private static final int PRODUCT_LOADER = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate UI view
		setContentView(R.layout.activity_catalog);

		// Setup FAB to open EditorActivity
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });

		// Find the ListView which will be populated with the pet data
		ListView productListView = findViewById(R.id.list);

		// Setup the item click listener
		productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Create new intent to go to EditorActivity
				Intent editIntent = new Intent(CatalogActivity.this, EditorActivity.class);

				// Construct URI with id
				Uri itemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

				// Package the URI into the intent
				editIntent.setData(itemUri);

				// Launch the activity
				startActivity(editIntent);
			}
		});

		// Find and set empty view on the ListView, so that it only shows when the list has 0 items.
		View emptyView = findViewById(R.id.empty_view);
		productListView.setEmptyView(emptyView);

		// Setup an Adapter to create a list item for each row of product data in the Cursor.
		mCursorAdapter = new ProductCursorAdapter(CatalogActivity.this, null);
		productListView.setAdapter(mCursorAdapter);

		// Kick off the loader
		getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Define a projection that specifies the columns from the table we care about.
		String[] projection = {
				ProductEntry._ID,
				ProductEntry.COLUMN_PRODUCT_DESC,
				ProductEntry.COLUMN_PRODUCT_QUANTITY,
				ProductEntry.COLUMN_PRODUCT_PRICE
		};

		// This loader will execute the ContentProvider's query method on a background thread
		return new CursorLoader(CatalogActivity.this,   // Parent activity context
				ProductEntry.CONTENT_URI,                       // Provider content URI to query
				projection,                                     // Columns to include in the resulting Cursor
				null,                                  // No selection clause
				null,                              // No selection arguments
				null);                                // Default sort order
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Update Adapter with this new cursor containing updated product data
		mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Loader is being destroyed or the data is no longer current.
		// Clear out adapter's reference to the cursor, prevents memory leaks
		mCursorAdapter.swapCursor(null);
	}
}
