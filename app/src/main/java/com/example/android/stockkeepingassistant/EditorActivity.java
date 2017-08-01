package com.example.android.stockkeepingassistant;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.stockkeepingassistant.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {

	private Spinner mSupplierContactSpinner;

	private String mSupplierContact;

	private EditText mProductDescEditText;

	private EditText mProductQuantityEditText;

	private EditText mProductPriceEditText;

	private EditText mProductSupplierEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup UI view
		setContentView(R.layout.activity_editor);

		// Get handle on the elements of the ViewGroup
		mProductDescEditText = (EditText) findViewById(R.id.editor_product_name);
		mProductQuantityEditText = (EditText) findViewById(R.id.editor_product_quantity);
		mProductPriceEditText = (EditText) findViewById(R.id.editor_product_price);
		mProductSupplierEditText = (EditText) findViewById(R.id.editor_product_supplier_name);
		mSupplierContactSpinner = (Spinner) findViewById(R.id.spinner_supplier_contact);
		// Set default text on quantity field
		mProductQuantityEditText.setText(String.valueOf(0));

		// Setup spinner
		setupSpinner();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Add menu items to app bar.
		getMenuInflater().inflate(R.menu.menu_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// User clicked on a menu option in the app bar overflow menu
		switch (item.getItemId()) {
			// Respond to a click on the "Save" menu option
			case R.id.action_save:
				// Save/update product
				saveProduct();
				// Exit activity
				finish();
				return true;
			// Respond to a click on the "Delete" menu option
			case R.id.action_delete:
				// Delete existing product
				return true;
			// Respond to a click on the "Up" arrow button in the app bar
			case android.R.id.home:
				// Back to CatalogActivity
				NavUtils.navigateUpFromSameTask(EditorActivity.this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Setup the dropdown spinner that allows the user to select the gender of the pet.
	 */
	private void setupSpinner() {
		// Create adapter for spinner. The list options are from the String array it will use
		// the spinner will use the default layout
		ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.array_supplier_contact_option, android.R.layout.simple_spinner_item);

		// Specify dropdown layout style - simple list view with 1 item per line
		genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		// Apply the adapter to the spinner
		mSupplierContactSpinner.setAdapter(genderSpinnerAdapter);

		// Set the integer mSelected to the constant values
		mSupplierContactSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selection = (String) parent.getItemAtPosition(position);
				if (!TextUtils.isEmpty(selection)) {
					if (selection.equals(getString(R.string.supplier_1))) {
						mSupplierContact= ProductEntry.SUPPLIER_1; // Supplier 1 email ID
					} else if (selection.equals(getString(R.string.supplier_2))) {
						mSupplierContact = ProductEntry.SUPPLIER_2; // Supplier 2 email ID
					} else if (selection.equals(getString(R.string.supplier_3))) {
						mSupplierContact = ProductEntry.SUPPLIER_3; // Supplier 3 email ID
					} else if (selection.equals(getString(R.string.supplier_4))) {
						mSupplierContact = ProductEntry.SUPPLIER_4; // Supplier 4 email ID
					} else if (selection.equals(getString(R.string.supplier_5))) {
						mSupplierContact = ProductEntry.SUPPLIER_5; // Supplier 5 email ID
					} else if (selection.equals(getString(R.string.supplier_6))) {
						mSupplierContact = ProductEntry.SUPPLIER_6; // Supplier 6 email ID
					} else if (selection.equals(getString(R.string.supplier_7))) {
						mSupplierContact = ProductEntry.SUPPLIER_7; // Supplier 7 email ID
					} else if (selection.equals(getString(R.string.supplier_8))) {
						mSupplierContact = ProductEntry.SUPPLIER_8; // Supplier 8 email ID
					} else if (selection.equals(getString(R.string.supplier_9))) {
						mSupplierContact = ProductEntry.SUPPLIER_9; // Supplier 9 email ID
					} else {
						mSupplierContact = ProductEntry.SUPPLIER_10; // Supplier 10 email ID
					}
				}
			}

			// Because AdapterView is an abstract class, onNothingSelected must be defined
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mSupplierContact = ProductEntry.SUPPLIER_1; // Supplier 1 email ID
			}
		});
	}

	private void saveProduct() {
		// Read from input fields
		// Use trim to eliminate leading or trailing white space
		String productDesc = mProductDescEditText.getText().toString().trim();
		String productQuantity = mProductQuantityEditText.getText().toString().trim();
		String productPrice = mProductPriceEditText.getText().toString().trim();
		String productSupplier = mProductSupplierEditText.getText().toString().trim();

		// Create a ContentValues object where column names are the keys,
		// and pet attributes from the editor are the values.
		ContentValues values = new ContentValues();
		values.put(ProductEntry.COLUMN_PRODUCT_DESC, productDesc);
		values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
		values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT, mSupplierContact);

		// If the quantity is not provided by the user, don't try to parse the string into an
		// integer value. Use 0 by default.
		int quantity = 0;
		if (!TextUtils.isEmpty(productQuantity)) {
			quantity = Integer.parseInt(productQuantity);
		}
		values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

		// If the quantity is not provided by the user, don't try to parse the string into an
		// integer value.
		if (!TextUtils.isEmpty(productPrice)) {
			int price = Integer.parseInt(productPrice);
			values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
		} else {
			Toast.makeText(
					EditorActivity.this,
					getString(R.string.editor_save_product_no_price),
					Toast.LENGTH_SHORT)
					.show();
		}

		// Insert new product into database
		getContentResolver().insert(ProductEntry.CONTENT_URI, values);

	}

	public void decreaseQuantity(View view) {
		// Get text from quantity field and parse as an integer
		int quantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());

		// Check for negative quantity
		if (quantity > 0) {
			// Quantity is not zero.
			// So, decrease quantity by 1.
			quantity--;
			mProductQuantityEditText.setText(String.valueOf(quantity));
		} else {
			// The product is currently not in stock and cannot be decreased.
			// Display message to the user that the quantity cannot be less than zero.
			Toast.makeText(EditorActivity.this, getString(R.string.editor_decrement_button_negative), Toast.LENGTH_SHORT).show();
		}
	}

	public void increaseQuantity(View view) {
		// Get text from quantity field and parse as an integer
		int quantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());

		// Decrease quantity by 1
		quantity++;

		// Set the revised quantity on the quantity field
		mProductQuantityEditText.setText(String.valueOf(quantity));
	}
}
