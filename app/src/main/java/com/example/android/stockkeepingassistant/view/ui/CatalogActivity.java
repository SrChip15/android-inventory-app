package com.example.android.stockkeepingassistant.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.model.Product;
import com.example.android.stockkeepingassistant.model.Warehouse;
import com.example.android.stockkeepingassistant.view.adapter.CatalogAdapter;

import java.util.List;

public class CatalogActivity
		extends AppCompatActivity
		/*implements LoaderCallbacks<Cursor>*/ {

	private RecyclerView recyclerView;
	private CatalogAdapter adapter;

	//private static final int PRODUCT_LOADER = 0;
	private static final String TAG = CatalogActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_catalog);

		// Setup FAB to open EditorActivity
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> {
			Product product = new Product();
			Warehouse.getInstance(this).addProduct(product);
            Intent intent = ProductActivity.newIntent(this, product.getId());
            startActivity(intent);
        });

		// Find the ListView which will be populated with the pet data
		recyclerView = findViewById(R.id.list);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		recyclerView.setHasFixedSize(true);

		// Setup the item click listener
		/*productListView.setOnItemClickListener((parent, view, position, id) -> {
		    Cursor c = (Cursor) parent.getItemAtPosition(position);

            // Create new intent to go to EditorActivity
            Intent editIntent = new Intent(CatalogActivity.this, EditorActivity.class);

            // Construct URI with id
			Log.d(TAG, "List Item: " + c.getCount());
			c.close();
            Uri itemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

            // Package the URI into the intent
            editIntent.setData(itemUri);

            // Launch the activity
            startActivity(editIntent);
        });*/

		// Find and set empty view on the ListView, so that it only shows when the list has 0 items.
		/*View emptyView = findViewById(R.id.empty_view);
		productListView.setEmptyView(emptyView);*/

		// Setup an Adapter to create a list item for each row of product data in the Cursor.
		Warehouse warehouse = Warehouse.getInstance(this);
		List<Product> products = warehouse.getProducts();

		adapter = new CatalogAdapter(this, products);
		recyclerView.setAdapter(adapter);

		/*// Kick off the loader
		getLoaderManager().initLoader(PRODUCT_LOADER, null, this);*/

	}

	@Override
	protected void onResume() {
		Warehouse warehouse = Warehouse.getInstance(this);
		List<Product> products = warehouse.getProducts();

		if (adapter == null) {
			adapter = new CatalogAdapter(this, products);
			recyclerView.setAdapter(adapter);
		} else {
			adapter.setData(products);
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	/*@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Define a projection that specifies the columns from the table we care about.
		String[] projection = {
				ProductEntry._ID,
				ProductEntry.COLUMN_PRODUCT_TITLE,
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
	}*/
}
