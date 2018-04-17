package com.example.android.stockkeepingassistant.view.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.Utils;
import com.example.android.stockkeepingassistant.model.Product;
import com.example.android.stockkeepingassistant.model.Warehouse;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
public class ProductFragment extends Fragment implements View.OnClickListener {
    /* Class variables */
    private ImageView productImage;
    private ImageButton productCamera;
    private EditText productTitle;
    private Button increaseQuantity;
    private EditText productQuantity;
    private Button decreaseQuantity;
    private EditText productPrice;
    private Spinner supplierName;
    private TextView supplierEmail;
    private Button orderMoreButton;
    private Product product;
    private Warehouse warehouse;
    private File photoFile;
    private int imageWidth;
    private int imageHeight;
    private int position;

    private static final String ARG_ID = "product_id";
    private static final int REQUEST_PHOTO = 2;

    public static ProductFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, productId);

        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            UUID productId = (UUID) Objects.requireNonNull(getArguments().getSerializable(ARG_ID));
            warehouse = Warehouse.getInstance(getActivity());
            product = warehouse.getProduct(productId);
            photoFile = warehouse.getPhotoFile(product);
        } else {
            throw new IllegalStateException("Fragment cannot be instantiated w/o product ID");
        }

        if (product.getSupplierName() != null) {
            String[] array = getActivity().getResources().getStringArray(R.array.array_supplier_contact_option);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(product.getSupplierName())) {
                    position = i;
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        productImage = view.findViewById(R.id.product_image);
        productImage.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                productImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageWidth = productImage.getMeasuredWidth();
                imageHeight = productImage.getMeasuredHeight();

                updateProductImage();
            }
        });

        productCamera = view.findViewById(R.id.product_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFile != null &&
                captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        productCamera.setEnabled(canTakePhoto);

        productCamera.setOnClickListener(v -> {
            Uri uri = FileProvider.getUriForFile(
                    getActivity(),
                    "com.example.android.stockkeepingassistant.fileprovider",
                    photoFile
            );
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> allCameraActivities =
                    getActivity()
                            .getPackageManager()
                            .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo cameraActivity : allCameraActivities) {
                getActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
            }

            startActivityForResult(captureImage, REQUEST_PHOTO);
        });

        productTitle = view.findViewById(R.id.product_title);
        productTitle.setText(product.getTitle());
        productTitle.addTextChangedListener(new GenericTextWatcher(productTitle));

        productQuantity = view.findViewById(R.id.product_quantity);
        productQuantity.setText(String.valueOf(product.getQuantity()));
        productQuantity.addTextChangedListener(new GenericTextWatcher(productQuantity));

        increaseQuantity = view.findViewById(R.id.product_increase_quantity);
        increaseQuantity.setOnClickListener(this);

        decreaseQuantity = view.findViewById(R.id.product_decrease_quantity);
        decreaseQuantity.setOnClickListener(this);

        productPrice = view.findViewById(R.id.product_price);
        productPrice.setText(product.getPrice().toPlainString());
        productPrice.addTextChangedListener(new GenericTextWatcher(productPrice));

        supplierName = view.findViewById(R.id.product_supplier_name);
        supplierEmail = view.findViewById(R.id.product_supplier_email);
        setupSpinner();

        orderMoreButton = view.findViewById(R.id.product_order_more);
        orderMoreButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            String subject = getString(R.string.product_order_more_email_subject).toUpperCase(Locale.getDefault());

            emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supplierEmail.getText().toString()});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

            if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(emailIntent);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(
                    getActivity(),
                    "com.example.android.stockkeepingassistant.fileprovider",
                    photoFile
            );

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateProductImage();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_product, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_product:
                warehouse.deleteProduct(product.getId());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateProductImage() {
        if (photoFile == null || !photoFile.exists()) {
            productImage.setImageBitmap(null);
        } else {
            Bitmap photo =
                    Utils.getScaledBitmap(
                            photoFile.getPath(),
                            imageWidth,
                            imageHeight
                    );
            productImage.setImageBitmap(photo);
        }
    }

    private void setupSpinner() {
        Context context = Objects.requireNonNull(getActivity());
        // Create adapter for spinner from the String array
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.array_supplier_contact_option,
                android.R.layout.simple_spinner_item
        );
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        supplierName.setAdapter(supplierSpinnerAdapter);
        if (product.getSupplierName() != null) {
            supplierName.setSelection(position);
        }

        // Get user selected supplier and map to app email ID
        supplierName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    product.setSupplierName(selection);
                    product.setSupplierEmail(warehouse.resolveEmail(selection));
                    supplierEmail.setText(product.getSupplierEmail());
                    warehouse.updateProduct(product);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (product.getSupplierName() == null) {
                    String firstInList = (String) parent.getItemAtPosition(0);
                    product.setSupplierName(firstInList);
                    product.setSupplierEmail(warehouse.resolveEmail(firstInList)); // Default is 1st in list
                    warehouse.updateProduct(product);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_increase_quantity:
                int units = Integer.parseInt(productQuantity.getText().toString().trim());
                productQuantity.setText(String.valueOf(++units));
                break;
            case R.id.product_decrease_quantity:
                int quantity = Integer.parseInt(productQuantity.getText().toString().trim());
                if (quantity > 0) {
                    productQuantity.setText(String.valueOf(--quantity));
                } else {
                    // The product is currently not in stock and cannot be decreased.
                    Toast.makeText(
                            getActivity(),
                            getString(R.string.product_decrease_button_negative),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        warehouse.updateProduct(product);
        super.onPause();
    }

    private class GenericTextWatcher implements TextWatcher {
        private View view;

        GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /* no-op */
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.product_title:
                    product.setTitle(s.toString());
                    break;
                case R.id.product_quantity:
                    product.setQuantity(Integer.valueOf(s.toString()));
                    break;
                case R.id.product_price:
                    if (s.length() > 0) {
                        product.setPrice(new BigDecimal(s.toString()));
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            /* no-op */
        }
    }
}
