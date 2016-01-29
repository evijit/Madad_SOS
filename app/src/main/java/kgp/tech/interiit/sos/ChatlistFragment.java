package kgp.tech.interiit.sos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import kgp.tech.interiit.sos.Utils.DateFormater;
import kgp.tech.interiit.sos.Utils.Helper;

public class ChatlistFragment extends Fragment {

    private Toolbar toolbar;
    private ListView listView;
    private List<ParseObject> sos_list = new ArrayList<ParseObject>();;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.frag_chatlist, container, false);

        listView = (ListView) v.findViewById(R.id.list_data);
        pullData();
        listView.setEmptyView(v.findViewById(R.id.emptyviewtxt));

        listView.setDivider(null);
        listView.setDividerHeight(0);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                //Get your item here with the position
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

//                    LayoutInflater inflater = Chatlist.this.getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.contact_card, null);
//                    dialogBuilder.setView(dialogView);
                String[] op = {"Archive", "Delete", "Block", "Details"};

                dialogBuilder.setItems(op, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 3)//Details
                        {
                            AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.contact_card, null);
                            dialogBuilder2.setView(dialogView);
                            AlertDialog alertDialog2 = dialogBuilder2.create();
                            alertDialog2.show();
                        }
                    }
                });


                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                //Get your item here with the position

                final Intent intent = new Intent(getActivity(), MessageActivity.class);
                ParseUser user = sos_list.get(position).getParseUser("UserID");
                intent.putExtra("channelID", sos_list.get(position).getString("channelID"));
                intent.putExtra("createdAt", DateFormater.formatTimeDate(sos_list.get(position).getCreatedAt()));
                intent.putExtra("username", user.getUsername());
                intent.putExtra("Description", user.getString("Description"));
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    void pullData()
    {
        ParseQuery<ParseObject> pq = ParseQuery.getQuery("SOS_Users");
        pq.whereEqualTo("UserID", ParseUser.getCurrentUser());
        pq.whereEqualTo("hasAccepted", true);
        pq.include("SOSid");
        pq.include("SOSid.UserID");
        pq.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                //Log.d("Chatlist", String.valueOf(list.size()));
                //Log.d("Chatlist", list.get(0).keySet().toString());

                for (ParseObject psos : list) {
                    ParseObject sos = psos.getParseObject("SOSid");
                    sos.pinInBackground();
                    sos_list.add(sos);
                }

                MyAdapter adapter = new MyAdapter(sos_list, getActivity());
                listView.setAdapter(adapter);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class MyAdapter extends BaseAdapter {

    private Context context;
    String[] txt,name;
    private List<ParseObject> sos_list;
//    int[] images={R.drawable.im0,R.drawable.im1,R.drawable.im2,R.drawable.im3,R.drawable.im4,R.drawable.im5,R.drawable.im6,R.drawable.im7,R.drawable.im8,R.drawable.im9,R.drawable.im10,R.drawable.im11,R.drawable.im12};


    MyAdapter(List<ParseObject> sos_list,Context context)
    {
        this.context=context;
        this.sos_list = sos_list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sos_list.size();
    }

    @Override
    public ParseObject getItem(int position) {
        // TODO Auto-generated method stub
        return sos_list.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row=inflater.inflate(R.layout.list_single, parent, false);

        TextView sos_title=(TextView) row.findViewById(R.id.name);
        TextView sos_message=(TextView) row.findViewById(R.id.txt);
        TextView sos_time=(TextView) row.findViewById(R.id.time);

        //Log.d("Chatlist","view "+sos_list.get(position).getClassName());
        final ParseUser user = sos_list.get(position).getParseUser("UserID");
        user.pinInBackground();

        sos_title.setText(user.getUsername());
        sos_message.setText(sos_list.get(position).getString("Description"));

        sos_time.setText(DateFormater.formatTime(sos_list.get(position).getCreatedAt()));

        CircleImageView iv1=(CircleImageView) row.findViewById(R.id.img);
        iv1.setImageResource(R.drawable.sample_man);
        Log.d("ChatList", "Pos " + position);
        Helper.GetProfilePic(user, iv1, context);
        Log.d("ChatList", "wow");
        return row;
    }



}

