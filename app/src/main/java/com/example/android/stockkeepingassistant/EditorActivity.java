package com.example.android.stockkeepingassistant;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.stockkeepingassistant.data.ProductContract.ProductEntry;

public class EditorActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor> {

	private Spinner mSupplierContactSpinner;

	private String mSupplierContact;

	private EditText mProductDescEditText;

	private EditText mProductQuantityEditText;

	private EditText mProductPriceEditText;

	private EditText mProductSupplierEditText;

	private Button mOrderMoreButton;

	/**
	 * Content URI for the existing product (null if it's a new product)
	 */
	private Uri mCurrentProductUri;

	private static final int EXISTING_PRODUCT_LOADER = 1;

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
		mOrderMoreButton = (Button) findViewById(R.id.editor_order_more);
		// Set default text on quantity field
		mProductQuantityEditText.setText(String.valueOf(0));

		// Setup spinner
		setupSpinner();

		// Identify the mode in which this activity is opened
		mCurrentProductUri = getIntent().getData();
		if (mCurrentProductUri != null) {
			// Detailed/Edit view mode active. Existing product is being viewed
			// Set appropriate title
			setTitle(R.string.editor_detailed_mode_title);

			// Kick-off loader to retrieve existing product information
			getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, EditorActivity.this);
		} else {
			// New product view mode active.
			// Set appropriate title
			setTitle(R.string.editor_new_product_mode_title);

			// Invalidate the options menu, so the "Delete" menu option can be hidden.
			invalidateOptionsMenu();

			// Remove "order more" button for new product
			mOrderMoreButton.setVisibility(View.GONE);
		}
	}

	/**
	 * This method is called after invalidateOptionsMenu(), so that the
	 * menu can be updated (some menu items can be hidden or made visible).
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// If this is a new product, hide the "Delete" menu item.
		if (mCurrentProductUri == null) {
			MenuItem menuItem = menu.findItem(R.id.action_delete);
			menuItem.setVisible(false);
		}
		return true;
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
				// Pop up confirmation dialog for deletion
				showDeleteConfirmationDialog();
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
						mSupplierContact = ProductEntry.SUPPLIER_1; // Supplier 1 email ID
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

		// Check if this is supposed to be a new pet
		// and check if all the fields in the editor are blank
		if (TextUtils.isEmpty(productDesc) && TextUtils.isEmpty(productQuantity) &&
				TextUtils.isEmpty(productPrice) && TextUtils.isEmpty(productSupplier)) {
			// Since no fields were modified, we can return early without creating a new product.
			// No need to create ContentValues and no need to do any ContentProvider operations.
			return;
		}

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

		if (mCurrentProductUri == null) {
			// This is a NEW product, so insert a new product into the provider,
			// returning the content URI for the new product.
			Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

			// Show a toast message depending on whether or not the insertion was successful.
			if (newUri == null) {
				// If the new content URI is null, then there was an error with insertion.
				Toast.makeText(EditorActivity.this,
						getString(R.string.editor_insert_product_failed),
						Toast.LENGTH_SHORT)
						.show();
			} else {
				// Otherwise, the insertion was successful and we can display a toast.
				Toast.makeText(this, getString(R.string.editor_insert_product_successful),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			// Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
			// and pass in the new ContentValues. Pass in null for the selection and selection args
			// because mCurrentProductUri will already identify the correct row in the database that
			// we want to modify.
			int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

			// Show a toast message depending on whether or not the update was successful.
			if (rowsAffected == 0) {
				// If no rows were affected, then there was an error with the update.
				Toast.makeText(this, getString(R.string.editor_update_product_failed),
						Toast.LENGTH_SHORT).show();
			} else {
				// Otherwise, the update was successful and we can display a toast.
				Toast.makeText(this, getString(R.string.editor_update_product_successful),
						Toast.LENGTH_SHORT).show();
			}
		}

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

	public void orderMore(View view) {
		// Get the position of the item the spinner is set at
		int position = mSupplierContactSpinner.getSelectedItemPosition();

		// Declare string to hold the supplier's email ID
		String supplierEmail;

		// Filter on spinner item position
		switch (position) {
			case 0:
				supplierEmail = ProductEntry.SUPPLIER_1;
				break;
			case 1:
				supplierEmail = ProductEntry.SUPPLIER_2;
				break;
			case 2:
				supplierEmail = ProductEntry.SUPPLIER_3;
				break;
			case 3:
				supplierEmail = ProductEntry.SUPPLIER_4;
				break;
			case 4:
				supplierEmail = ProductEntry.SUPPLIER_5;
				break;
			case 5:
				supplierEmail = ProductEntry.SUPPLIER_6;
				break;
			case 6:
				supplierEmail = ProductEntry.SUPPLIER_7;
				break;
			case 7:
				supplierEmail = ProductEntry.SUPPLIER_8;
				break;
			case 8:
				supplierEmail = ProductEntry.SUPPLIER_9;
				break;
			default:
				supplierEmail = ProductEntry.SUPPLIER_10;
				break;
		}

		// Create email intent
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

		// Create subject for email
		String subject = getString(R.string.editor_order_more_email_subject).toUpperCase();

		// Set email ID on the intent
		emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {supplierEmail});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

		// Launch the activity
		if (emailIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(emailIntent);
		}
	}

	/**
	 * Prompt the user to confirm that they want to delete this pet.
	 */
	private void showDeleteConfirmationDialog() {
		// Create an AlertDialog.Builder and set the message, and click listeners
		// for the postivie and negative buttons on the dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_dialog_msg);
		builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked the "Delete" button, so delete the product.
				deletePet();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked the "Cancel" button, so dismiss the dialog
				// and continue editing the pet.
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		// Create and show the AlertDialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	/**
	 * Perform the deletion of the pet in the database.
	 */
	private void deletePet() {
		// Only perform the delete if this is an existing product.
		if (mCurrentProductUri != null) {
			// Call the ContentResolver to delete the product at the given content URI.
			// Pass in null for the selection and selection args because the mCurrentProductUri
			// content URI already identifies the product that we want.
			int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

			// Show a toast message depending on whether or not the delete was successful.
			if (rowsDeleted == 0) {
				// If no rows were deleted, then there was an error with the delete.
				Toast.makeText(this, getString(R.string.editor_delete_product_failed),
						Toast.LENGTH_SHORT).show();
			} else {
				// Otherwise, the delete was successful and we can display a toast.
				Toast.makeText(this, getString(R.string.editor_delete_product_successful),
						Toast.LENGTH_SHORT).show();
			}
		}

		// Close the activity
		finish();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Since the editor shows all product attributes, define a projection that contains
		// all columns from the pet table
		String[] projection = {
				ProductEntry._ID,
				ProductEntry.COLUMN_PRODUCT_DESC,
				ProductEntry.COLUMN_PRODUCT_QUANTITY,
				ProductEntry.COLUMN_PRODUCT_PRICE,
				ProductEntry.COLUMN_PRODUCT_SUPPLIER,
				ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT
		};

		// This loader will execute the ContentProvider's query method on a background thread
		return new CursorLoader(this,           // Parent activity context
				mCurrentProductUri,             // Query the content URI for the current pet
				projection,                     // Columns to include in the resulting Cursor
				null,                           // No selection clause
				null,                           // No selection arguments
				null);                          // Default sort order
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Bail early if the cursor is null or there is less than 1 row in the cursor
		if (cursor == null || cursor.getCount() < 1) {
			return;
		}

		// Move to first row
		if (cursor.moveToFirst()) {
			// Find the columns of product attributes that we're interested in
			int descColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DESC);
			int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
			int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
			int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
			int supplierContactColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT);

			// Extract out the value from the Cursor for the given column index
			String desc = cursor.getString(descColumnIndex);
			int quantity = cursor.getInt(quantityColumnIndex);
			int price = cursor.getInt(priceColumnIndex);
			String supplier = cursor.getString(supplierColumnIndex);
			String supplierContact = cursor.getString(supplierContactColumnIndex);

			// Update the views on the screen with the values from the database
			mProductDescEditText.setText(desc);
			mProductQuantityEditText.setText(String.valueOf(quantity));
			mProductPriceEditText.setText(String.valueOf(price));
			mProductSupplierEditText.setText(String.valueOf(supplier));

			// Supplier contact information is a spinner drop down.
			// Map the constant value from database into one of the drop-down options
			switch (supplierContact) {
				case ProductEntry.SUPPLIER_1:
					mSupplierContactSpinner.setSelection(0);
					break;
				case ProductEntry.SUPPLIER_2:
					mSupplierContactSpinner.setSelection(1);
					break;
				case ProductEntry.SUPPLIER_3:
					mSupplierContactSpinner.setSelection(2);
					break;
				case ProductEntry.SUPPLIER_4:
					mSupplierContactSpinner.setSelection(3);
					break;
				case ProductEntry.SUPPLIER_5:
					mSupplierContactSpinner.setSelection(4);
					break;
				case ProductEntry.SUPPLIER_6:
					mSupplierContactSpinner.setSelection(5);
					break;
				case ProductEntry.SUPPLIER_7:
					mSupplierContactSpinner.setSelection(6);
					break;
				case ProductEntry.SUPPLIER_8:
					mSupplierContactSpinner.setSelection(7);
					break;
				case ProductEntry.SUPPLIER_9:
					mSupplierContactSpinner.setSelection(8);
					break;
				default:
					mSupplierContactSpinner.setSelection(9);
					break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// If the loader is invalidated, clear out all the data from the input fields.
		mProductDescEditText.setText("");
		mProductQuantityEditText.setText("");
		mProductPriceEditText.setText("");
		mProductSupplierEditText.setText("");
		mSupplierContactSpinner.setSelection(0);
	}
}
