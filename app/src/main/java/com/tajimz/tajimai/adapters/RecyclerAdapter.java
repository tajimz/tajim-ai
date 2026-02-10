package com.tajimz.tajimai.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tajimz.tajimai.MainActivity;
import com.tajimz.tajimai.databinding.ItemRecyclerBinding;
import com.tajimz.tajimai.models.ChatModel;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import io.noties.markwon.Markwon;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    Context context;
    List<ChatModel> list;
    RecyclerView recyclerView;
    TextToSpeech tts;
    Markwon markwon;

    public RecyclerAdapter(Context context, List<ChatModel> list, RecyclerView recyclerView, TextToSpeech tts){
        this.context = context;
        this.list = list;
        this.recyclerView = recyclerView;
        this.tts = tts;
        markwon = Markwon.create(context);


    }
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerHolder(ItemRecyclerBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        holder.binding.tvAi.setVisibility(GONE);
        holder.binding.iconsParent.setVisibility(GONE);
        holder.binding.tvUser.setVisibility(GONE);
        holder.binding.tvAi.setText("");
        holder.binding.tvUser.setText("");
        holder.binding.tvAi.setMovementMethod(LinkMovementMethod.getInstance());


        ChatModel chatModel = list.get(position);
        if (chatModel.getIsAi()){
            holder.binding.tvAi.setVisibility(VISIBLE);
            if (chatModel.getShouldTyping()){
                animateTyping(chatModel.getAi(), holder.binding.tvAi, 25, holder.binding.iconsParent);
                chatModel.setShouldNotTyping();


            }else {

                holder.binding.tvAi.setText(chatModel.getAi());

            }

        }else {
            holder.binding.tvUser.setVisibility(VISIBLE);
            holder.binding.tvUser.setText(chatModel.getUser());

        }

        holder.binding.imgCopy.setOnClickListener(v -> {

                copyTextToClip(chatModel.getAi(), holder.binding.imgCopy);


        });

        holder.binding.imgVoice.setOnClickListener(v->{
            if (!speechEnabled){
                startTextToSpeech(chatModel.getAi());
            }else {
                endTextToSpeech();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder{
        ItemRecyclerBinding binding;
        public RecyclerHolder(ItemRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private void animateTyping(String message, TextView textView, long delayMillis, ConstraintLayout constraintLayout) {
        textView.setText("");
        StringBuilder currentText = new StringBuilder();
        Handler handler = new Handler(Looper.getMainLooper());

        for (int i = 0; i < message.length(); i++) {
            final int index = i;
            handler.postDelayed(() -> {
                currentText.append(message.charAt(index));
                markwon.setMarkdown(textView, currentText.toString());

                if (index == message.length() - 1 && context instanceof MainActivity) {
                    ((MainActivity) context).enableButton();
                    recyclerView.scrollToPosition(list.size() - 1);
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            }, delayMillis * i);
        }

    }

    private void copyTextToClip(String text, View anchorView) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        androidx.appcompat.widget.TooltipCompat.setTooltipText(anchorView, "Copied");

        anchorView.post(() -> anchorView.performLongClick());

        anchorView.postDelayed(() ->
                androidx.appcompat.widget.TooltipCompat.setTooltipText(anchorView, null), 1000);
    }


    public boolean speechEnabled = false;
    private void startTextToSpeech(String text){
        speechEnabled = true;
        tts.speak(text,TextToSpeech.QUEUE_FLUSH, null, "tts1");
    }
    private void endTextToSpeech(){
        speechEnabled = false;
        tts.stop();
    }



}