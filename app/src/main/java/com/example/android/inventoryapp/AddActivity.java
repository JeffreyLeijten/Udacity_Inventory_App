package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static com.example.android.inventoryapp.data.ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE;

public class AddActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 0;

    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierEmailEditText;
    private ImageView mProductImageView;

    private Uri mCurrentPetUri;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mNameEditText = (EditText) findViewById(R.id.name_add_text);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_add_text);
        mPriceEditText = (EditText) findViewById(R.id.price_add_text);
        mSupplierEditText = (EditText) findViewById(R.id.supplier_add_text);
        mSupplierEmailEditText = (EditText) findViewById(R.id.supplier_email_add_text);
        mProductImageView = (ImageView) findViewById(R.id.product_image_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_picture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_select_picture)), IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mImageUri = data.getData();
                mProductImageView.setImageURI(mImageUri);
                mProductImageView.invalidate();
            }
        }
    }

    private void saveProduct(){
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(supplierString)&&
                TextUtils.isEmpty(supplierEmailString) && mImageUri == null) {
            return;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.toast_require_name), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.toast_require_quantity), Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = Integer.parseInt(quantityString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.toast_require_price), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);

        if (mImageUri == null) {
            Toast.makeText(this, getString(R.string.toast_require_image), Toast.LENGTH_SHORT).show();
            return;
        }
        String image = mImageUri.toString();
        values.put(COLUMN_PRODUCT_IMAGE, image);

        if (TextUtils.isEmpty(supplierString)) {
            Toast.makeText(this, getString(R.string.toast_require_supplier), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);

        if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, getString(R.string.toast_require_supplier_email), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
