package com.karmies.karmiesexample;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.karmies.karmiessdk.Karmies;
import com.karmies.karmiessdk.activity.KarmiesActivity;
import com.karmies.karmiessdk.view.KarmiesEditText;
import com.karmies.karmiessdk.view.chat.KarmiesTextView;

import java.util.ArrayList;

public class MainActivity extends KarmiesActivity implements View.OnClickListener {

    private KarmiesEditText karmiesEditText;
    private RecyclerView messagesList;
    private ArrayList<String> messages = new ArrayList<String>();
    private MessageListAdapter adapter = new MessageListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize Karmies prior to activity create
        Karmies.create(this, "default", true, true);

        // Create activity and load layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Always show soft keyboard for Karmies even when hardware keyboard is present
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // Enable Karmies auto suggest bar
        karmiesEditText = (KarmiesEditText) findViewById(R.id.custom_edit_text);
        karmiesEditText.setAutoSuggest(true);

        // Add click listener for send button to send message
        findViewById(R.id.send_image_button).setOnClickListener(this);

        // Add click listener for Karmies in conversation message list
        messagesList = (RecyclerView) findViewById(R.id.messages_list);
        messagesList.setLayoutManager(new LinearLayoutManager(this));
        messagesList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_image_button:

                // Get serialized message text from input and add to messages
                String text = karmiesEditText.getTextWithEmojis(karmiesEditText.getEditableText(), true);
                if (text == null || text.length() <=0) {
                    return;
                }
                messages.add(text);
                adapter.notifyItemInserted(messages.size() - 1);
        }
    }

    private class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_message_list_item, viewGroup, false);
            return new MessageItem(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            // Set message text
            ((MessageItem)holder).messageText.setKarmiesText(messages.get(position));

            // Send message impressions
            Karmies.getAnalytics().sendMessageImpressionEventsIdempotent(
                    messages.get(position), messages.get(position), true);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    private class MessageItem extends RecyclerView.ViewHolder {

        public KarmiesTextView messageText;

        public MessageItem(View itemView) {
            super(itemView);

            // Assign message text
            messageText = (KarmiesTextView) itemView.findViewById(R.id.message_text);
        }
    }
}
