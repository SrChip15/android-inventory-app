package com.example.android.stockkeepingassistant;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

	private Spinner mSupplierContactSpinner;

	private String mSupplierContact;

	private Button mDecreaseQuantityButton;

	private EditText mProductQuantity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup UI view
		setContentView(R.layout.activity_editor);

		// Get handle on the elements of the ViewGroup
		mSupplierContactSpinner = (Spinner) findViewById(R.id.spinner_supplier_contact);
		mProductQuantity = (EditText) findViewById(R.id.editor_product_quantity);
		// Set default text on quantity field
		mProductQuantity.setText(String.valueOf(0));

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

		/*// Set the integer mSelected to the constant values
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
		});*/
	}

	public void decreaseQuantity(View view) {
		// Get text from quantity field and parse as an integer
		int quantity = Integer.parseInt(mProductQuantity.getText().toString().trim());

		// Check for negative quantity
		if (quantity > 0) {
			// Quantity is not zero.
			// So, decrease quantity by 1.
			quantity--;
			mProductQuantity.setText(String.valueOf(quantity));
		} else {
			// The product is currently not in stock and cannot be decreased.
			// Display message to the user that the quantity cannot be less than zero.
			Toast.makeText(EditorActivity.this, getString(R.string.editor_decrement_button_negative), Toast.LENGTH_SHORT).show();
		}
	}

	public void increaseQuantity(View view) {
		// Get text from quantity field and parse as an integer
		int quantity = Integer.parseInt(mProductQuantity.getText().toString().trim());

		// Decrease quantity by 1
		quantity++;

		// Set the revised quantity on the quantity field
		mProductQuantity.setText(String.valueOf(quantity));
	}
}
