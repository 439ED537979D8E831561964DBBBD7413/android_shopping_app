package course.android.letgo_307945402_204317770.GUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import course.android.letgo_307945402_204317770.Logic.ChatAdapter;
import course.android.letgo_307945402_204317770.Logic.SysData;
import course.android.letgo_307945402_204317770.Objects.Chat;
import course.android.letgo_307945402_204317770.R;

public class ChatLogsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SysData data;
    private ArrayList<Chat> chats;
    private ChatAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlogs);

        data = SysData.getInstance();

        if (data.dbIsClosed()) {
            data.openDB(this);
            data.initUser();
            data.initChats();
        }

        adapter = new ChatAdapter(this, data.getChats(), data.getUser());
        recyclerView = (RecyclerView)findViewById(R.id.recycler_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }
}
