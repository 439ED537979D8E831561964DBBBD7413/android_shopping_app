package course.android.shopping_example_app.Logic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import course.android.shopping_example_app.GUI.MessagesActivity;
import course.android.shopping_example_app.Objects.Chat;
import course.android.shopping_example_app.Objects.User;
import course.android.shopping_example_app.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<Chat> chatList;
    private Context context;
    private User user;

    public ChatAdapter(Context context, ArrayList<Chat> chatList, User user){
        this.context = context;
        this.chatList = chatList;
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_log_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        if(user.equals(chat.getGuest())){

            setImage(holder, chat.getSender());
            holder.name.setText(chat.getSender().getFullName());
        }else {
            setImage(holder, chat.getGuest());
            holder.name.setText(chat.getGuest().getFullName());
        }


        String str = chat.getLastMessage().getMessage();
        if(str.length() > 20) {
            str = str.substring(0, 20);
            str += "...";
        }


        holder.massege.setText(str);

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private void setImage(final ViewHolder holder, final User user) {
        if (user.getThumb() != null)
            holder.thumb.setImageBitmap(user.getThumb());
        else {
            holder.thumb.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView thumb;
        private TextView name;
        private TextView massege;

        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (CircleImageView) itemView.findViewById(R.id.prof_chat_thumb);
            name = (TextView) itemView.findViewById(R.id.name_chat_row);
            massege = (TextView) itemView.findViewById(R.id.msg_chat_row);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessagesActivity.class);
                    intent.putExtra("index", getAdapterPosition());
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ChatAdapter.this.removeDialog(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    private void removeDialog(final int adapterPosition) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNegativeButton(R.string.answerNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.answerYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(SysData.getInstance().discardChat(chatList.get(adapterPosition)))
                            notifyDataSetChanged();

                        dialog.dismiss();
                    }
                }).setTitle(R.string.askContinue);
        dialog = builder.create();
        dialog.show();
    }


}
