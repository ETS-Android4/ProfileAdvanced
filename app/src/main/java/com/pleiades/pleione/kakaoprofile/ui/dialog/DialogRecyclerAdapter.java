package com.pleiades.pleione.kakaoprofile.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;

import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_PROFILES;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SORT_PROFILES;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.DialogViewHolder> {
    private String[] messages;
    private int checkedPosition, contentsType;

    // constructor
    DialogRecyclerAdapter(String[] messages, int contentsType) {
        this.messages = messages;
        this.contentsType = contentsType;

        if (contentsType == DIALOG_SORT_PROFILES)
            checkedPosition = PrefsController.getInt(KEY_SORT_PROFILES);
        else if (contentsType == DIALOG_SORT_ARCHIVE)
            checkedPosition = PrefsController.getInt(KEY_SORT_ARCHIVE);
    }

    // attach view with holder
    class DialogViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        RadioButton radioButton;

        DialogViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.dialog_list_message);
            radioButton = view.findViewById(R.id.dialog_list_radio);

            // attach click interface
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition(); // dialog list position
                    if((position != RecyclerView.NO_POSITION) && (itemClickListener != null)){
                        itemClickListener.onItemClick(view, position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_component_dialog_list, viewGroup, false);
        return new DialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder holder, int position) {
        // set text
        holder.message.setText(messages[position]);

        switch(contentsType){
            case DIALOG_SORT_PROFILES:
            case DIALOG_SORT_ARCHIVE:
                holder.radioButton.setVisibility(View.VISIBLE);
                if (position == checkedPosition)
                    holder.radioButton.setChecked(true);
                else
                    holder.radioButton.setChecked(false);
                break;
            default:
                holder.radioButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.length;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
//        context = recyclerView.getContext();
    }

    // click listener interface for outside of adapter
    private OnItemClickListener itemClickListener = null;

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }
}