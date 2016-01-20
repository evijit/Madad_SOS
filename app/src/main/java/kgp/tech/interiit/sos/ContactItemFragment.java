package kgp.tech.interiit.sos;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/**
 * Created by akshaygupta on 20/01/16.
 */
public class ContactItemFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String[] PROJECTION =
    {
            Data._ID,
            Data.MIMETYPE,
            Data.DATA1,
            Data.DATA2,
            Data.DATA3,
            Data.DATA4,
            Data.DATA5,
            Data.DATA6,
            Data.DATA7,
            Data.DATA8,
            Data.DATA9,
            Data.DATA10,
            Data.DATA11,
            Data.DATA12,
            Data.DATA13,
            Data.DATA14,
            Data.DATA15
    };

    // Defines the selection clause
    private static final String SELECTION = Data.LOOKUP_KEY + " = ?";
    // Defines the array to hold the search criteria
    private String[] mSelectionArgs = { "" };
    /*
     * Defines a variable to contain the selection value. Once you
     * have the Cursor from the Contacts table, and you've selected
     * the desired row, move the row's LOOKUP_KEY value into this
     * variable.
     */
    private String mLookupKey;

    /*
     * Defines a string that specifies a sort order of MIME type
     */
    private static final String SORT_ORDER = Data.MIMETYPE;

    // Defines a constant that identifies the loader
    final private int DETAILS_QUERY_ID = 0;
    /*
     * Invoked when the parent Activity is instantiated
     * and the Fragment's UI is ready. Put final initialization
     * steps here.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Initializes the loader framework
        //getLoaderManager().initLoader(DETAILS_QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Choose the proper action
        switch (id) {
            case DETAILS_QUERY_ID:
                // Assigns the selection parameter
                mSelectionArgs[0] = mLookupKey;
                // Starts the query
                CursorLoader mLoader =
                        new CursorLoader(
                                getActivity(),
                                Data.CONTENT_URI,
                                PROJECTION,
                                SELECTION,
                                mSelectionArgs,
                                SORT_ORDER
                        );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAILS_QUERY_ID:
                    /*
                     * Process the resulting Cursor here.
                     */
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DETAILS_QUERY_ID:
                /*
                 * If you have current references to the Cursor,
                 * remove them here.
                 */
                break;
        }
    }
}
