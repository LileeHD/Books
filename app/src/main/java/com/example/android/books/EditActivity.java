package com.example.android.books;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.books.data.BookContract.BookEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityTextView;

    private EditText mSupplierNameEditText;

    private EditText mSupplierPhoneEditText;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

//        Figure which activity was call
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

//        set the activities attributes respectively
        if (mCurrentBookUri == null) {
            setTitle("Add a new book");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit a book details");
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

//find the proper views to populate
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_price);
        mQuantityTextView = findViewById(R.id.text_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

/**
 * TODO: menu options
 * TODO: prevent blank fields
 * TODO: switch between edit or new entry
 */

//        Save and call buttons onClickListener
        Button incrementBtn = findViewById(R.id.increment);
        final Button decrementBtn = findViewById(R.id.decrement);
        int qty = 0;
        mQuantityTextView.setText(String.valueOf(qty));
        //Set click listener on the increase button and increase the quantity
        //according the adjustment factor, check for validation before changing data
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = Integer.parseInt(mQuantityTextView.getText().toString().trim());
                mQuantityTextView.setText(String.valueOf(newQuantity + 1));
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = Integer.parseInt(mQuantityTextView.getText().toString().trim());
                mQuantityTextView.setText(String.valueOf(newQuantity - 1));
                if (newQuantity == 1) {
                    decrementBtn.setEnabled(false);
                }
            }
        });

        TextView saveBtn = findViewById(R.id.save_btn);
        TextView callBtn = findViewById(R.id.call_btn);
        if (mCurrentBookUri == null) {
            callBtn.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savy();
                finish();
            }
        });

        /**
         * it Works
         */
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supPhoneString = mSupplierPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supPhoneString));
                startActivity(intent);
            }
        });
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_current_book);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_current_book:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveBook() {
//        input fields
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String supNameString = mSupplierNameEditText.getText().toString().trim();
        String supPhoneString = mSupplierPhoneEditText.getText().toString().trim();
//        db content values
        ContentValues values = new ContentValues();

        values.put(BookEntry.BOOK_NAME, nameString);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(BookEntry.BOOK_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookEntry.BOOK_QUANTITY, quantity);

        values.put(BookEntry.BOOK_SUPPLIER_NAME, supNameString);

        long phone = 0;
        if (!TextUtils.isEmpty(supPhoneString)) {
            phone = Long.parseLong(supPhoneString);
        }
        values.put(BookEntry.BOOK_SUPPLIER_PHONE, phone);

        //        if it's a new entry
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supNameString) &&
                TextUtils.isEmpty(supPhoneString)) {

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error while saving entry", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "New book is saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(BookEntry.CONTENT_URI, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error while updating entry", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book successfully updated", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void savy() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String supNameString = mSupplierNameEditText.getText().toString().trim();
        String supPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(supNameString) || TextUtils.isEmpty(supPhoneString)) {
            Toast.makeText(this, "Please fill every fields", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(BookEntry.BOOK_NAME, nameString);

            int price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }
            values.put(BookEntry.BOOK_PRICE, price);

            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            values.put(BookEntry.BOOK_QUANTITY, quantity);

            values.put(BookEntry.BOOK_SUPPLIER_NAME, supNameString);

            long phone = 0;
            if (!TextUtils.isEmpty(supPhoneString)) {
                phone = Long.parseLong(supPhoneString);
            }
            values.put(BookEntry.BOOK_SUPPLIER_PHONE, phone);
            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, "Editor failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "It swell !",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
                if (rowAffected == 0) {
                    Toast.makeText(this, "Editor failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "It swell !",
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    private void deleteBook() {
        // TODO: Implement this method
        if (mCurrentBookUri != null) {
            int rowDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowDeleted == 0) {
                Toast.makeText(this, "Edit failed on this one", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book successfully edited.", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            String supPhone = cursor.getString(supPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supName);
            mSupplierPhoneEditText.setText(supPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
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
}
