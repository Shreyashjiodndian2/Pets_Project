package com.example.petsproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petsproject.data.PetContract.PetEntry;

public class PetProvider extends ContentProvider {

    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String LOG_TAG = PetProvider.class.getSimpleName();

    static {
        sURI_MATCHER.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sURI_MATCHER.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    SQLiteDatabase database;
    private PetDbHelper mPetDbHelper;

    @Override
    public boolean onCreate() {
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        database = mPetDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PETS:
                cursor = database.query(PetEntry.TABLE_NAME, projection, null, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(uri + "is wrong");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                return insert_pet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not support for uri" + uri);
        }
    }

    private Uri insert_pet(Uri uri, ContentValues values) {
        String name = values.getAsString(PetEntry.COLUMN_NAME);
        if (name == null) {
            Toast.makeText(getContext(), "Please fill Name field", Toast.LENGTH_SHORT);
            throw new IllegalArgumentException("Name is compulsory");
        }
        int gender = values.getAsInteger(PetEntry.COLUMN_GENDER);
        if (!(gender == PetEntry.GENDER_FEMALE || gender == PetEntry.GENDER_MALE || gender == PetEntry.GENDER_UNKNOWN)) {
            Toast.makeText(getContext(), "Please fill Gender field", Toast.LENGTH_SHORT);
            throw new IllegalArgumentException("Gender is compulsory");
        }
        Integer weight = values.getAsInteger(PetEntry.COLUMN_WEIGHT);
        if (null == weight || weight < 0) {
            Toast.makeText(getContext(), "Please fill Weight field", Toast.LENGTH_SHORT);
            throw new IllegalArgumentException("Weight is compulsory");
        }
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        long id = database.insert(PetEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "This insertion is not possible");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return delete_pet(uri, selection, selectionArgs);
            case PETS:
                return delete_pet(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion not possible");
        }
    }

    private int delete_pet(Uri uri, String selection, String[] selectionArgs) {
        database = mPetDbHelper.getWritableDatabase();
        int rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return update_pet(PetEntry.TABLE_NAME, values, selection, selectionArgs, uri);
            case PETS:
                return update_pet(PetEntry.TABLE_NAME, values, selection, selectionArgs, uri);
            default:
                throw new IllegalArgumentException("invalid uri for update");
        }
    }

    private int update_pet(String tableName, ContentValues values, String selection, String[] selectionArgs, Uri uri) {
        if (values.containsKey(PetEntry.COLUMN_NAME)) {
            if (values.getAsString(PetEntry.COLUMN_NAME) == null) {
                throw new IllegalArgumentException("PLease enter name");
            }
        }
        if (values.containsKey(PetEntry.COLUMN_GENDER)) {
            int gender = values.getAsInteger(PetEntry.COLUMN_GENDER);
            if (gender != PetEntry.GENDER_MALE && gender != PetEntry.GENDER_FEMALE && gender != PetEntry.GENDER_UNKNOWN) {
                throw new IllegalArgumentException("Please enter correct gender");
            }
        }
        if (values.containsKey(PetEntry.COLUMN_WEIGHT)) {
            if (values.getAsInteger(PetEntry.COLUMN_WEIGHT) == null) {
                throw new IllegalArgumentException("Please enter correct weight");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        int update_row = database.update(tableName, values, selection, selectionArgs);
        if (update_row != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return update_row;
    }
}
