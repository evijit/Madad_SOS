package kgp.tech.interiit.sos;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by akshaygupta on 20/01/16.
 */
public class ContactItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public ContactItemFragment(){}

    public OnContactsInteractionListener mSelectedListener;

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
    private static final String SELECTION = Data.CONTACT_ID + " = ?";
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
        super.onActivityCreated(savedInstanceState);
        Log.i("Info", "CF");
        // Initializes the loader framework
        getLoaderManager().initLoader(DETAILS_QUERY_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Choose the proper action
        switch (id) {
            case DETAILS_QUERY_ID:
                // Assigns the selection parameter
                mSelectionArgs[0] = mLookupKey;
                Log.i("Info","Key"+mLookupKey);
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
                return mLoader;
        }
        return null;
    }

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Info", "Cont Fragment Created");
        // Inflate the fragment layout

        return inflater.inflate(R.layout.contact_card,
                container, false);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("Info","Load Finished");
        switch (loader.getId()) {
            case DETAILS_QUERY_ID:
                    /*
                     * Process the resulting Cursor here.
                     */
                Log.i("Info",String.valueOf(data.getCount()));
                while(data.moveToFirst())
                {
                    for(int i=0;i<PROJECTION.length;i++)
                        Log.i("Info","data "+data.getString(i));
                }

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

    public void setLookupKey(String lookupKey)
    {

        mLookupKey = lookupKey;
    }

    public interface OnContactsInteractionListener {
        /**
         * Called when a contact is selected from the ListView.
         * @param contactUri The contact Uri.
         */
        public void onContactSelected(String lookupKey);

        /**
         * Called when the ListView selection is cleared like when
         * a contact search is taking place or is finishing.
         */
        public void onSelectionCleared();
    }
}
