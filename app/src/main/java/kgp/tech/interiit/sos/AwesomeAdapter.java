package kgp.tech.interiit.sos;

/**
 * Created by Avijit Ghosh on 24-02-2015.
 */


        import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

//import android.os.Message;

public class AwesomeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Message> mMessages;



    public AwesomeAdapter(Context context, ArrayList<Message> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
    }
    @Override
    public int getCount() {
        return mMessages.size();
    }
    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = (Message) this.getItem(position);

        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.message.setText(message.getMessage());

        LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
        //check if it is a status message then remove background, and change text color.
        if(message.isStatusMessage())
        {
            holder.message.setBackgroundDrawable(null);
            lp.gravity = Gravity.LEFT;
            //holder.message.setTextColor(R.color.textFieldColor);
        }
        else
        {
            //Check whether message is mine to show green background and align to right
            if(message.isMine())
            {
                holder.message.setBackgroundResource(R.color.primary);
                holder.message.setTextColor(Color.parseColor("#ffffff"));
                lp.gravity = Gravity.RIGHT;
            }
            //If not mine then it is from sender to show orange background and align to left
            else
            {

                //Drawable d=R.drawable.bubble_your;
                //Drawable.mutate().setColorFilter( 0xffff0000, Mode.MULTIPLY)
                holder.message.setBackgroundResource(android.R.color.white);
                holder.message.setTextColor(Color.parseColor("#000000"));
                lp.gravity = Gravity.LEFT;
            }
            holder.message.setLayoutParams(lp);
            //holder.message.setTextColor(R.color.textColor);
        }
        return convertView;
    }
    private static class ViewHolder
    {
        TextView message;
    }

    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return position;
    }

}
