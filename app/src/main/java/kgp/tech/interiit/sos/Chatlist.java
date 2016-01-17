package kgp.tech.interiit.sos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chatlist extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(new SpannableString("Chats"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView) findViewById(R.id.list_data);
        listView.setEmptyView(findViewById(R.id.emptyviewtxt));

        MyAdapter adapter = new MyAdapter(getApplicationContext());
        listView.setAdapter(adapter);

        listView.setDivider(null);
        listView.setDividerHeight(0);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                //Get your item here with the position
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());

                    /*LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.contact_card, null);
                    dialogBuilder.setView(dialogView);*/
                String[] op = {"Archive", "Delete", "Block", "Details"};

                dialogBuilder.setItems(op, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 3) {
                            AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(Chatlist.this);
                            LayoutInflater inflater = getLayoutInflater();
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

                final Intent intent = new Intent(Chatlist.this, MessageActivity.class);

                //FloatingActionsMenu fm = ((FloatingActionsMenu) getActivity().findViewById(R.id.new_up));

                TextView tv1 = (TextView) v.findViewById(R.id.name);
                String name = tv1.getText().toString();
                intent.putExtra("name", name);
                startActivity(intent);


            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatlist, menu);
        return true;
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
//    int[] images={R.drawable.im0,R.drawable.im1,R.drawable.im2,R.drawable.im3,R.drawable.im4,R.drawable.im5,R.drawable.im6,R.drawable.im7,R.drawable.im8,R.drawable.im9,R.drawable.im10,R.drawable.im11,R.drawable.im12};


    MyAdapter(Context context)
    {
        this.context=context;
        name=context.getResources().getStringArray(R.array.sample_names);
        txt=context.getResources().getStringArray(R.array.sample_text);
//        shuffleArray(images);


        Random rng = new Random();
        List<String> arr = Arrays.asList(txt);
        Collections.shuffle(arr, rng);
        arr.toArray(txt);


        rng = new Random();
        arr = Arrays.asList(name);
        Collections.shuffle(arr, rng);
        arr.toArray(name);
    }



    static void shuffleArray(int[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return name[position];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View row=null;


        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.list_single, parent, false);
        }
        else
        {
            row=convertView;
        }
        TextView tv1=(TextView) row.findViewById(R.id.name);
        TextView tv2=(TextView) row.findViewById(R.id.txt);
        TextView tv3=(TextView) row.findViewById(R.id.time);
        CircleImageView iv1=(CircleImageView) row.findViewById(R.id.img);


        tv1.setText(name[position]);
        tv2.setText(txt[position]);
        iv1.setImageResource(R.drawable.sample_man);

        Random r = new Random();
        int Low = 0;
        int High = 23;
        int R1 = r.nextInt(High-Low) + Low;
        String f1 = String.format("%02d", R1);
        Low = 00;
        High=59;
        int R2 = r.nextInt(High-Low) + Low;
        String f2 = String.format("%02d", R2);
        tv3.setText(f1+":"+f2);

        return row;
    }

}

