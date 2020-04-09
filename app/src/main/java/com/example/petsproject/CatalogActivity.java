package com.example.petsproject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.petsproject.data.PetContract.PetEntry;
import com.example.petsproject.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int LOADER_ID = 0;
    private PetCursorAdapter petCursorAdapter;
    private PetDbHelper petDbHelper = new PetDbHelper(this);
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        final ListView listView = findViewById(R.id.text_view_pet);
        listView.setEmptyView(findViewById(R.id.empty_view));
        petCursorAdapter = new PetCursorAdapter(this, null);
        listView.setAdapter(petCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void showOnDeleteConfirmationMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_message);
        builder.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllPet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void deleteAllPet() {
        int delete_all = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        if (delete_all == 0) {
            Toast.makeText(this, R.string.delete_All_Failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.delete_all_successful, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insert_pet() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetEntry.COLUMN_NAME, "Toto");
        contentValues.put(PetEntry.COLUMN_BREED, "Terrier");
        contentValues.put(PetEntry.COLUMN_GENDER, PetEntry.GENDER_MALE);
        contentValues.put(PetEntry.COLUMN_WEIGHT, 7);
        getContentResolver().insert(PetEntry.CONTENT_URI, contentValues);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insert_pet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                showOnDeleteConfirmationMessage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        String[] pet_projection = {PetEntry._ID, PetEntry.COLUMN_NAME, PetEntry.COLUMN_BREED};
        return new CursorLoader(this, PetEntry.CONTENT_URI,
                pet_projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        petCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        petCursorAdapter.swapCursor(null);
    }
}