package kgp.tech.interiit.sos;
/**
 * Created by Avijit Ghosh on 24-02-2015.
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

//import android.app.ListActivity;

/**
 * MessageActivity is a main Activity to show a ListView containing Message items
 *
 * @author Adil Soomro
 *
 */
public class MessageActivity extends AppCompatActivity {
    /** Called when the activity is first created. */

    ArrayList<Message> messages;
    AwesomeAdapter adapter;
    EditText text;
    static Random rand = new Random();
    static String sender;
    private Toolbar toolbar;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        SpannableString s = new SpannableString("");
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(s);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //sender = Utility.sender[rand.nextInt( Utility.sender.length-1)];

        }

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null && toolbar!=null)
        {
            String j =(String) b.get("name");
            SpannableString s1 = new SpannableString(j);
            TextView title= (TextView)findViewById(R.id.toolbar_title);
            title.setText(j);
            toolbar.findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MessageActivity.this);
// ...Irrelevant code for customizing the buttons and title
                    LayoutInflater inflater = MessageActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.contact_card, null);
                    dialogBuilder.setView(dialogView);


                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                    // Toast.makeText(getApplicationContext(), "CONTACTS CARD TO BE SHOWN. WORK IN PROGRESS.",
                    //         Toast.LENGTH_LONG).show();
                }
            });
            //getSupportActionBar().setTitle(s1);
            sender=j;
        }

        listView = (ListView) findViewById(R.id.list);
        text = (EditText) this.findViewById(R.id.text);

        /*sender = Utility.sender[rand.nextInt( Utility.sender.length-1)];
        this.setTitle(sender);*/
        messages = new ArrayList<Message>();

        messages.add(new Message("Hello", false));
        messages.add(new Message("Hi!", true));
        messages.add(new Message("Wassup??", false));
        messages.add(new Message("nothing much, working on speech bubbles.", true));
        messages.add(new Message("you say!", true));
        messages.add(new Message("oh thats great. how are you showing them", false));


        adapter = new AwesomeAdapter(this, messages);
        listView.setAdapter(adapter);
        addNewMessage(new Message("mmm, well, using 9 patches png to show them.", true));
    }
    public void sendMessage(View v)
    {
        String newMessage = text.getText().toString().trim();
        if(newMessage.length() > 0)
        {
            text.setText("");
            addNewMessage(new Message(newMessage, true));
            new SendMessage().execute();
        }
    }
    private class SendMessage extends AsyncTask<Void, String, String>
    {
        String sender="Somebody";
        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000); //simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.publishProgress(String.format("%s started writing", sender));
            try {
                Thread.sleep(2000); //simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.publishProgress(String.format("%s has entered text", sender));
            try {
                Thread.sleep(3000);//simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }


            return Random_Message.messages[rand.nextInt(Random_Message.messages.length-1)];


        }
        @Override
        public void onProgressUpdate(String... v) {

            if(messages.get(messages.size()-1).isStatusMessage)//check wether we have already added a status message
            {
                messages.get(messages.size()-1).setMessage(v[0]); //update the status for that
                adapter.notifyDataSetChanged();
                listView.setSelection(messages.size()-1);
            }
            else{
                addNewMessage(new Message(true,v[0])); //add new message, if there is no existing status message
            }
        }
        @Override
        protected void onPostExecute(String text) {
            if(messages.get(messages.size()-1).isStatusMessage)//check if there is any status message, now remove it.
            {
                messages.remove(messages.size()-1);
            }

            addNewMessage(new Message(text, false)); // add the orignal message from server.
        }


    }
    void addNewMessage(Message m)
    {
        messages.add(m);
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size()-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//
//            case R.id.action_attach:
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                startActivityForResult(intent,1);
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    //textFile.setText(FilePath);
                    Toast.makeText(getApplicationContext(), FilePath,
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

}
