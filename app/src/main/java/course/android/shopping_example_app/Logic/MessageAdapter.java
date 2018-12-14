package course.android.shopping_example_app.Logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import course.android.shopping_example_app.Objects.Message;
import course.android.shopping_example_app.R;
import course.android.shopping_example_app.Utills.DateUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private ArrayList<Message> msgs;


    public MessageAdapter(Context context, ArrayList<Message> msgs){
        this.msgs = msgs;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        // adapting view type to message if sent or received
        if (msgs.get(position).getSender().equals(SysData.getInstance().getUser())){
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = msgs.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public void updateMessages(ArrayList<Message> messages) {

        msgs = messages;
        notifyDataSetChanged();

    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(DateUtil.formatDateTime(message.getDateFormat()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            profileImage = (CircleImageView) itemView.findViewById(R.id.image_message_profile);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            if(message.getSender().getThumbnail() != null)
                profileImage.setImageBitmap(message.getSender().getThumbnail());
            else
                profileImage.setImageResource(R.drawable.ic_account_circle_black_24dp);

            nameText.setText(message.getSender().getFullName());
            messageText.setText(message.getMessage());
            timeText.setText(DateUtil.formatDateTime(message.getDateFormat()));
        }
    }
}
