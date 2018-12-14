package course.android.shopping_example_app.GUI;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import course.android.shopping_example_app.Logic.MessageAdapter;
import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Objects.Chat;
import shopping_example_app.R;
import course.android.shopping_example_app.Services.NotifyierService;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageAdapter mMessageAdapter;
    private EditText text;
    private SysData data;
    private int index;
    private Chat chat;
    private BroadcastReceiver onNotice;
    private boolean subbed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        subbed = false;

        index = getIntent().getIntExtra("index", -1);
        data = SysData.getInstance();

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        text = (EditText) findViewById(R.id.edittext_chatbox);

        if(index >= 0){
            loadActivity(index);

        }else
            finish();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        loadActivity(intent.getIntExtra("index", -1));
    }

    private void loadActivity(int index) {
        if(index >=0 ){
            chat = data.getChats().get(index);
            data.getChatMsgsFromDB(chat);
            if(data.getUser().equals(chat.getSender())) {
                getSupportActionBar().setTitle(chat.getGuest().getFullName());
            }
            else {
                getSupportActionBar().setTitle(chat.getSender().getFullName());
            }

            mMessageAdapter = new MessageAdapter(this, chat.getMessages());
            mMessageRecycler.setAdapter(mMessageAdapter);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
        }

    }

    public void addMassage(View view) {
        String str = text.getText().toString();

        if(!str.isEmpty()){
            if (data.snedMessage(chat, str)) {
                mMessageAdapter. notifyDataSetChanged();
                text.getText().clear();
                mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!subbed) {

            // register receiver to refresh messages in activity
            IntentFilter filter = new IntentFilter(NotifyierService.BROADCAST);
            onNotice = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    data.getChatMsgsFromDB(chat);
                    mMessageAdapter.updateMessages(chat.getMessages());
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
                    ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotifyierService.MY_NOTIFICATION_ID);
                }

            };
            registerReceiver(onNotice, filter);
            subbed = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister the receiver
        if(subbed) {
            unregisterReceiver(onNotice);
            onNotice = null;
            subbed = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(chat != null) {
            if (chat.getMessages().isEmpty())
                data.discardChat(chat);
        }
    }
}
