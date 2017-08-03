package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PRODUCT_LOADER = 0;

    private int quantity;
    private String supplierEmail;

    private Uri currentProductUri;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_shipping);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        Button increaseButton = (Button) findViewById(R.id.increase_button);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity += 1;
                setQuantity(quantity);
            }
        });

        Button decreaseButton = (Button) findViewById(R.id.decrease_button);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(quantity == 0)){
                    quantity -= 1;
                    setQuantity(quantity);
                }
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void sendMail( ){
        Intent eMailIntent = new Intent(Intent.ACTION_SENDTO);
        eMailIntent.setData(Uri.parse("mailto:"));
        String[] addresses = new String[1];
        addresses[0] = supplierEmail;
        eMailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (eMailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(eMailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setQuantity(int newQuantity){
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.details_update_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.detail_update_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

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
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Define a projection that specifies the columns from the table we care about.
            String[] projection = {
                    ProductEntry._ID,
                    ProductEntry.COLUMN_PRODUCT_NAME,
                    ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    ProductEntry.COLUMN_PRODUCT_PRICE,
                    ProductEntry.COLUMN_PRODUCT_IMAGE,
                    ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                    ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL };

            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader(this,   // Parent activity context
                    currentProductUri,   // Provider content URI to query
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);                  // Default sort order
        }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);

            TextView nameTextView = (TextView) findViewById(R.id.name_details_text_view);
            TextView quantityTextView = (TextView) findViewById(R.id.quantity_details_text_view);
            TextView priceTextView = (TextView) findViewById(R.id.price_details_text_view);
            ImageView imageView = (ImageView) findViewById(R.id.details_image_view);
            TextView supplierTextView = (TextView) findViewById(R.id.supplier_details_text_view);
            TextView supplierEmailTextView = (TextView) findViewById(R.id.supplier_email_details_text_view);

            String productName = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String productQuantity = "Quantity: " + quantity;
            String productPrice = "Price: $" + cursor.getString(priceColumnIndex);
            String productImage = cursor.getString(imageColumnIndex);
            imageUri = Uri.parse(productImage);
            String productSupplier = "Supplier: " + cursor.getString(supplierColumnIndex);
            supplierEmail = cursor.getString(supplierEmailColumnIndex);
            String productSupplierEmail = "Supplier Email: " + supplierEmail;

            nameTextView.setText(productName);
            quantityTextView.setText(productQuantity);
            priceTextView.setText(productPrice);
            imageView.setImageURI(imageUri);
            supplierTextView.setText(productSupplier);
            supplierEmailTextView.setText(productSupplierEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
