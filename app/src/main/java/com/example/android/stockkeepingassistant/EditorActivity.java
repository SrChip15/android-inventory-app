package com.example.android.stockkeepingassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.stockkeepingassistant.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class EditorActivity
		extends AppCompatActivity
		implements LoaderCallbacks<Cursor> {

	public static final String TAG = EditorActivity.class.getSimpleName();
	private static final int EXISTING_PRODUCT_LOADER = 1;
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

	/** Spinner for choosing supplier contact */
	private Spinner supplierEmailPicker;

	/** Supplier email ID. Possible values are available in the ProductContract.java file */
	private String supplierEmail;

	/** EditText field for product description */
	private EditText productTitle;

	/** EditText field for product quantity information */
	private EditText productQuantity;

	/** EditText field for product price information */
	private EditText productPrice;

	/** EditText field for product supplier's name */
	private EditText supplierName;

	/** EditText field for product image */
	private ImageView productImage;

	/** Button to upload product image */
	private Button productCamera;

	/** Intent identifier */
	private static final int PICK_IMG_CODE = 1;

	/** Product image as byte array to be stored in database */
	private byte[] imageAsArray = null;

	/** Boolean flag that tracks whether the product has been edited (true) or not (false) */
	private boolean productHasChanged = false;

	/**
	 * OnTouchListener that listens for any user touches on a View, implying that they are modifying
	 * the view, and we change the productHasChanged boolean to true.
	 */
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			productHasChanged = true;
			return false;
		}
	};

	/** Content URI for the existing product (null if it's a new product) */
	private Uri mCurrentProductUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup UI view
		setContentView(R.layout.activity_editor);

		// Get handle on the elements of the ViewGroup
		productTitle = findViewById(R.id.editor_product_name);
		productQuantity = findViewById(R.id.editor_product_quantity);
		productPrice = findViewById(R.id.editor_product_price);
		supplierName = findViewById(R.id.editor_product_supplier_name);
		supplierEmailPicker = findViewById(R.id.spinner_supplier_contact);
		Button mOrderMoreButton = findViewById(R.id.editor_order_more);
		productImage = findViewById(R.id.editor_product_image);
		productCamera = findViewById(R.id.editor_upload_image_button);
		Button increaseQuantityButton = findViewById(R.id.editor_quantity_increment);
		Button decreaseQuantityButton = findViewById(R.id.editor_quantity_decrement);

		// Setup OnTouchListeners on all the input fields, so we can determine if the user
		// has touched or modified them. This will let us know if there are unsaved changes
		// or not, if the user tries to leave the editor without saving.
		productTitle.setOnTouchListener(mTouchListener);
		productQuantity.setOnTouchListener(mTouchListener);
		productPrice.setOnTouchListener(mTouchListener);
		supplierName.setOnTouchListener(mTouchListener);
		supplierEmailPicker.setOnTouchListener(mTouchListener);
		productCamera.setOnTouchListener(mTouchListener);
		increaseQuantityButton.setOnTouchListener(mTouchListener);
		decreaseQuantityButton.setOnTouchListener(mTouchListener);

		// Set default text on quantity field
		productQuantity.setText(String.valueOf(0));

		// Setup spinner
		setupSpinner();


		// If the list item was clicked then the product URI of the list item is set on the intent
		// that triggered the editor activity
		mCurrentProductUri = getIntent().getData();

		// Determine the mode in which the editor activity was launched
		if (mCurrentProductUri != null) {
			// Detailed/Edit view mode active. Existing product is being viewed
			// Set appropriate title
			setTitle(R.string.editor_detailed_mode_title);

			// Remove upload photo button from view
			productCamera.setVisibility(View.GONE);

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

			// Remove product image view
			productImage.setVisibility(View.GONE);
		}
	}

	/**
	 * This method is called after invalidateOptionsMenu(), so that the
	 * menu can be updated (some menu items can be hidden or made visible).
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// This is a new product
		if (mCurrentProductUri == null) {
			// Hide the "Delete" menu item
			MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
			deleteMenuItem.setVisible(false);

			// Hide the "Add Image" menu item
			MenuItem addImageMenuItem = menu.findItem(R.id.action_add_image);
			addImageMenuItem.setVisible(false);
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
			case R.id.action_add_image:
				// Add image if default image is being displayed.
				// Edit image if a better image is available.
				addOrEditImage();
				return true;
			// Respond to a click on the "Up" arrow button in the app bar
			case android.R.id.home:
				// If the product hasn't changed, continue with navigating up to parent activity
				if (!productHasChanged) {
					NavUtils.navigateUpFromSameTask(EditorActivity.this);
					return true;
				}

				// Otherwise if there are unsaved changes, setup a dialog to warn the user.
				// Create a click listener to handle the user confirming that
				// changes should be discarded.
				DialogInterface.OnClickListener discardButtonClickListener =
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								// User clicked "Discard" button, navigate to parent activity.
								NavUtils.navigateUpFromSameTask(EditorActivity.this);
							}
						};

				// Show a dialog that notifies the user they have unsaved changes
				showUnsavedChangesDialog(discardButtonClickListener);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method is called when the back button is pressed.
	 */
	@Override
	public void onBackPressed() {
		// If the product hasn't changed, continue with handling back button press
		if (!productHasChanged) {
			super.onBackPressed();
			return;
		}

		// Otherwise if there are unsaved changes, setup a dialog to warn the user.
		// Create a click listener to handle the user confirming that changes should be discarded.
		DialogInterface.OnClickListener discardButtonClickListener =
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						// User clicked "Discard" button, close the current activity.
						finish();
					}
				};

		// Show dialog that there are unsaved changes
		showUnsavedChangesDialog(discardButtonClickListener);
	}

	/**
	 * Setup the dropdown spinner that allows the user to select the supplier's email.
	 */
	private void setupSpinner() {
		// Create adapter for spinner. The list options are from the String array it will use
		// the spinner will use the default layout
		ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.array_supplier_contact_option, android.R.layout.simple_spinner_item);

		// Specify dropdown layout style - simple list view with 1 item per line
		genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		// Apply the adapter to the spinner
		supplierEmailPicker.setAdapter(genderSpinnerAdapter);

		// Set the integer mSelected to the constant values
		supplierEmailPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selection = (String) parent.getItemAtPosition(position);
				if (!TextUtils.isEmpty(selection)) {
					if (selection.equals(getString(R.string.supplier_1))) {
						supplierEmail = ProductEntry.SUPPLIER_1; // Supplier 1 email ID
					} else if (selection.equals(getString(R.string.supplier_2))) {
						supplierEmail = ProductEntry.SUPPLIER_2; // Supplier 2 email ID
					} else if (selection.equals(getString(R.string.supplier_3))) {
						supplierEmail = ProductEntry.SUPPLIER_3; // Supplier 3 email ID
					} else if (selection.equals(getString(R.string.supplier_4))) {
						supplierEmail = ProductEntry.SUPPLIER_4; // Supplier 4 email ID
					} else if (selection.equals(getString(R.string.supplier_5))) {
						supplierEmail = ProductEntry.SUPPLIER_5; // Supplier 5 email ID
					} else if (selection.equals(getString(R.string.supplier_6))) {
						supplierEmail = ProductEntry.SUPPLIER_6; // Supplier 6 email ID
					} else if (selection.equals(getString(R.string.supplier_7))) {
						supplierEmail = ProductEntry.SUPPLIER_7; // Supplier 7 email ID
					} else if (selection.equals(getString(R.string.supplier_8))) {
						supplierEmail = ProductEntry.SUPPLIER_8; // Supplier 8 email ID
					} else if (selection.equals(getString(R.string.supplier_9))) {
						supplierEmail = ProductEntry.SUPPLIER_9; // Supplier 9 email ID
					} else {
						supplierEmail = ProductEntry.SUPPLIER_10; // Supplier 10 email ID
					}
				}
			}

			// Because AdapterView is an abstract class, onNothingSelected must be defined
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				supplierEmail = ProductEntry.SUPPLIER_1; // Supplier 1 email ID
			}
		});
	}

	private void saveProduct() {
		// Read from input fields
		// Use trim to eliminate leading or trailing white space
		String productDesc = productTitle.getText().toString().trim();
		String productQuantity = this.productQuantity.getText().toString().trim();
		String productPrice = this.productPrice.getText().toString().trim();
		String productSupplier = supplierName.getText().toString().trim();

		// Create a ContentValues object where column names are the keys,
		// and product attributes from the editor are the values.
		ContentValues values = new ContentValues();
		values.put(ProductEntry.COLUMN_PRODUCT_DESC, productDesc);
		values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
		values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT, supplierEmail);

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

			// Bail early
			return;
		}

		// Check if product image has been provided and if provided add the image URI as
		// string to the database
		if (imageAsArray != null) {
			// Product image is provided
			values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageAsArray);

			// Clear current item's image URI
			imageAsArray = null;
		}

		if (mCurrentProductUri == null) {
			// This is a NEW product, so insert a new product into the provider,
			// returning the content URI for the new product.

			// Check if this is supposed to be a new product
			// and check if all the fields in the editor are blank
			if (TextUtils.isEmpty(productDesc) || productDesc == null && TextUtils.isEmpty(productQuantity) || productQuantity == null &&
					TextUtils.isEmpty(productPrice) || productPrice == null && TextUtils.isEmpty(productSupplier) || productSupplier == null) {
				// Since no fields were modified, we can return early without creating a new product.
				// No need to create ContentValues and no need to do any ContentProvider operations.

				return;

			} else {
				// Validation is complete, so insert new product into database
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
		int quantity = Integer.parseInt(productQuantity.getText().toString().trim());

		// Check for negative quantity
		if (quantity > 0) {
			// Quantity is not zero.
			// So, decrease quantity by 1.
			quantity--;
			productQuantity.setText(String.valueOf(quantity));
		} else {
			// The product is currently not in stock and cannot be decreased.
			// Display message to the user that the quantity cannot be less than zero.
			Toast.makeText(EditorActivity.this, getString(R.string.editor_decrement_button_negative), Toast.LENGTH_SHORT).show();
		}
	}

	public void increaseQuantity(View view) {
		// Get text from quantity field and parse as an integer
		int quantity = Integer.parseInt(productQuantity.getText().toString().trim());

		// Increase quantity by 1
		quantity++;

		// Set the revised quantity on the quantity field
		productQuantity.setText(String.valueOf(quantity));
	}

	public void orderMore(View view) {
		// Get the position of the item the spinner is set at
		int position = supplierEmailPicker.getSelectedItemPosition();

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
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supplierEmail});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

		// Launch the activity
		if (emailIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(emailIntent);
		}
	}

	private void addOrEditImage() {
		// Create intent
		Intent imageFromGalleryIntent = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		// Launch activity.
		// Starts the device's gallery in "select photo" mode
		startActivityForResult(imageFromGalleryIntent, PICK_IMG_CODE);

		// Add image to values
		ContentValues editImageValue = new ContentValues();
		editImageValue.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageAsArray);

		// update db
		getContentResolver().update(mCurrentProductUri, editImageValue, null, null);

		// Remove image associated with imageAsArray
		imageAsArray = null;
	}

	/** Prompt the user to confirm that they want to delete this product. */
	private void showDeleteConfirmationDialog() {
		// Create an AlertDialog.Builder and set the message, and click listeners
		// for the postivie and negative buttons on the dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_dialog_msg);
		builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked the "Delete" button, so delete the product.
				deleteProduct();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked the "Cancel" button, so dismiss the dialog
				// and continue editing the product.
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
	 * Perform the deletion of the product in the database.
	 */
	private void deleteProduct() {
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

	/**
	 * Show a dialog that warns the user there are unsaved changes that will be lost
	 * if they continue leaving the editor.
	 */
	private void showUnsavedChangesDialog(
			DialogInterface.OnClickListener discardButtonClickListener) {
		// Create an AlertDialog.Builder and set the message, and click listeners
		// for the postivie and negative buttons on the dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.unsaved_changes_dialog_msg);
		builder.setPositiveButton(R.string.discard, discardButtonClickListener);
		builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked the "Keep editing" button, so dismiss the dialog
				// and continue editing the product.
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		// Create and show the AlertDialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Since the editor shows all product attributes, define a projection that contains
		// all columns from the products table
		String[] projection = {
				ProductEntry._ID,
				ProductEntry.COLUMN_PRODUCT_DESC,
				ProductEntry.COLUMN_PRODUCT_IMAGE,
				ProductEntry.COLUMN_PRODUCT_QUANTITY,
				ProductEntry.COLUMN_PRODUCT_PRICE,
				ProductEntry.COLUMN_PRODUCT_SUPPLIER,
				ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT
		};

		// This loader will execute the ContentProvider's query method on a background thread
		return new CursorLoader(this,           // Parent activity context
				mCurrentProductUri,             // Query the content URI for the current product
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
			int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
			int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
			int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
			int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
			int supplierContactColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT);

			// Extract out the value from the Cursor for the given column index
			String desc = cursor.getString(descColumnIndex);
			byte[] byteImage = cursor.getBlob(imageColumnIndex);
			int quantity = cursor.getInt(quantityColumnIndex);
			int price = cursor.getInt(priceColumnIndex);
			String supplier = cursor.getString(supplierColumnIndex);
			String supplierContact = cursor.getString(supplierContactColumnIndex);

			// Update the views on the screen with the values from the database
			productTitle.setText(desc);
			productQuantity.setText(String.valueOf(quantity));
			productPrice.setText(String.valueOf(price));
			supplierName.setText(String.valueOf(supplier));

			// Initialize var to hold product image
			if (byteImage != null) {
				Bitmap image;

				// Get image from gallery
				image = getImage(byteImage);

				// Set image on product image view
				productImage.setImageBitmap(image);
			} else {
				// Product image does not exist.
				// Set default image for product image. This image information is not saved to
				// database, purely UI fix.
				productImage.setImageResource(R.drawable.no_prod_img);
			}

			// Supplier contact information is a spinner drop down.
			// Map the constant value from database into one of the drop-down options
			switch (supplierContact) {
				case ProductEntry.SUPPLIER_1:
					supplierEmailPicker.setSelection(0);
					break;
				case ProductEntry.SUPPLIER_2:
					supplierEmailPicker.setSelection(1);
					break;
				case ProductEntry.SUPPLIER_3:
					supplierEmailPicker.setSelection(2);
					break;
				case ProductEntry.SUPPLIER_4:
					supplierEmailPicker.setSelection(3);
					break;
				case ProductEntry.SUPPLIER_5:
					supplierEmailPicker.setSelection(4);
					break;
				case ProductEntry.SUPPLIER_6:
					supplierEmailPicker.setSelection(5);
					break;
				case ProductEntry.SUPPLIER_7:
					supplierEmailPicker.setSelection(6);
					break;
				case ProductEntry.SUPPLIER_8:
					supplierEmailPicker.setSelection(7);
					break;
				case ProductEntry.SUPPLIER_9:
					supplierEmailPicker.setSelection(8);
					break;
				default:
					supplierEmailPicker.setSelection(9);
					break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// If the loader is invalidated, clear out all the data from the input fields.
		productTitle.setText("");
		productQuantity.setText("");
		productPrice.setText("");
		supplierName.setText("");
		supplierEmailPicker.setSelection(0);
		productImage.setImageBitmap(null);
	}

	/**
	 * The method is invoked when the user clicks the upload product image button. The
	 * product image is expected to be saved to the device's gallery.
	 */
	public void uploadProductImageClick(View view) {
		if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
			// do your stuff..

			// Create intent to pick image from gallery
			Intent imageFromGalleryIntent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			// Launch activity.
			// Starts the device's gallery in "select photo" mode
			startActivityForResult(imageFromGalleryIntent, PICK_IMG_CODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {

			switch (requestCode) {
				case PICK_IMG_CODE:
					if (resultCode == RESULT_OK) {
						Uri selectedImage = imageReturnedIntent.getData();
						String[] filePathColumn = {MediaStore.Images.Media.DATA};

						Cursor cursor = getContentResolver().query(
								selectedImage,
								filePathColumn,
								null,
								null,
								null
						);
						cursor.moveToFirst();

						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						String filePath = cursor.getString(columnIndex);
						cursor.close();

						Log.v(TAG, "Found Image at: " + filePath);

						Bitmap yourSelectedImage = null;

						try {
							yourSelectedImage = decodeUri(selectedImage);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						// Display image to user within new product edit window
						productImage.setImageBitmap(yourSelectedImage);
						productImage.setVisibility(View.VISIBLE);

						// If existing product is being updated
						if (mCurrentProductUri != null) {
							Toast.makeText(
									EditorActivity.this,
									getString(R.string.editor_product_image_update_success),
									Toast.LENGTH_SHORT
							)
									.show();
						}
						// Product image information has changed, so set it to true
						productHasChanged = true;

						// Remove upload button from view
						productCamera.setVisibility(View.GONE);

						// Save the image to global variable image and clear it out after saving the
						// current product item
						imageAsArray = getBytes(yourSelectedImage);
					}
			}
		}
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE
					|| height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
	}

	/**
	 * Helper method to conver bitmap to byte array
	 */
	private byte[] getBytes(@NonNull Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

	/**
	 * Helper method to convert byte array to bitmap
	 */
	private Bitmap getImage(byte[] image) {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}

	public boolean checkPermissionREAD_EXTERNAL_STORAGE(
			final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						(Activity) context,
						Manifest.permission.READ_EXTERNAL_STORAGE)) {
					showDialog(context,
							Manifest.permission.READ_EXTERNAL_STORAGE);

				} else {
					ActivityCompat
							.requestPermissions(
									(Activity) context,
									new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}

		} else {
			return true;
		}
	}

	public void showDialog(final Context context,
	                       final String permission) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setCancelable(true);
		alertBuilder.setTitle("Permission necessary");
		alertBuilder.setMessage("External storage" + " permission is necessary");
		alertBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActivityCompat.requestPermissions((Activity) context,
								new String[] { permission },
								MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
					}
				});
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
				if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
					Toast.makeText(EditorActivity.this, "Media Permission Denied",
							Toast.LENGTH_SHORT).show();
					return;
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions,
						grantResults);
		}
	}
}
