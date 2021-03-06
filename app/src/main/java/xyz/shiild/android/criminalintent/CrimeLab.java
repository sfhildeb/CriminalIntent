package xyz.shiild.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.shiild.android.criminalintent.database.CrimeBaseHelper;
import xyz.shiild.android.criminalintent.database.CrimeCursorWrapper;
import xyz.shiild.android.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * A centralized data stash for Crime objects.
 *
 * @author Stephen Hildebrand
 * @version 7/7/2016
 */
public class CrimeLab {
    /** A static CrimeLab variable for the CrimeLab singleton */
    private static CrimeLab sCrimeLab;
    /** Context instance variable...to be used in chapter 16. */
    private Context mContext;
    /** Storage for the crime database. */
    private SQLiteDatabase mDatabase;

    /**
     * Private constructor for the singleton CrimeLab. Retrieves the current application context,
     * then calls getWritableDatabase to open/initialize the database.
     * @param context The context to initialize with.
     */
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    /**
     * Add a new crime. Gets the crime's ContentValues then inserts the values into the specified
     * table (via CrimeTable.NAME).
     * @param c The crime to add.
     */
    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        // CrimeTable.NAME is the table you want to insert into.
        // values is the data that you want to put in.
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    /**
     * Deletes the crime. The uuidString is not directly put into the where clause to avoid
     * SQL injection. So the where clause includes a " = ?" to check that the values specified
     * are String values. As a result, rather than pass in uuidString directly it is passed in
     * as a String[].
     * @param c The crime to delete.
     */
    public void deleteCrime(Crime c) {
        String uuidString = c.getId().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID
                + " = ?", new String[] { uuidString });
    }

    /**
     * Returns the list of crimes. Walks the cursor down the list of crimes and adds each
     * Crime to a list of Crimes.
     * @return The list of crimes.
     */
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            // Walk the cursor down the list of crimes
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    /**
     * Searches the list of crimes for a Crime with the given ID. Only pulls the first item,
     * if it is there.
     * @param id The ID associated with the Crime to search for.
     * @return The Crime with the given ID, or null if not found.
     */
    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    /**
     * A method to update the rows in the database. The uuidString is not directly put into the
     * where clause in case the String itself might contain SQL code. If that String were to be
     * put directly in your query it could change the meaning of your query or even later the
     * database. This is called a SQL injection attack. If you use ?, then the code will act as
     * intended and treat it as a String value, not as code.
     * @param crime The crime to update.
     */
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        // Also specifies which rows should be updated by building a where clause (the 3rd arg),
        // and then specifying values for the arguments in the where clause
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID
                + " = ?", new String[] { uuidString });
    }

    /**
     * Private method for shuttling a Crime into a ContentValues. Creates the ContentValues, then
     * puts each of the four keys with its associated column with it into it.
     * @param crime The Crime to be added
     * @return the ContentValues key-value pair
     */
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }

    /**
     * Wraps the cursor returned from the query in a CrimeCursorWrapper, then iterates over it
     * while calling getCrime to pull out its Crimes.
     * @param whereClause Specifies which columns get updated.
     * @param whereArgs The arguments to update with.
     * @return The CrimeCursorWrapper containing the query results.
     */
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause, // Specify which columns get updated
                whereArgs, // The arguments to update the columns with
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }
}