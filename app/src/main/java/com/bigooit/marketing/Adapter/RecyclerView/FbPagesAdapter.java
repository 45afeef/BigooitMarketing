package com.bigooit.marketing.Adapter.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigooit.marketing.DataModels.FbPage;
import com.bigooit.marketing.R;

import java.util.ArrayList;

public class FbPagesAdapter extends RecyclerView.Adapter<FbPagesAdapter.ViewHolder> {

    private final ArrayList<FbPage> fbPages;
    private FbPageHandler pageHandler;

    /**
     * Make Interface
     */

    public interface FbPageHandler{
        void onClickFbPage(FbPage page);
    }

    /**
     * Initialize the fbPageArrayList of the Adapter.
     *
     * @param fbPageArrayList String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public FbPagesAdapter(ArrayList<FbPage> fbPageArrayList,FbPageHandler context) {
        this.fbPages = fbPageArrayList;
        this.pageHandler = context;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView pageNameView;
        private final TextView pageIdView;

        public ViewHolder(View view) {
            super(view);
            pageNameView = view.findViewById(R.id.fb_page_name);
            pageIdView = view.findViewById(R.id.fb_page_id);
        }
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_fb_page, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        FbPage page = fbPages.get(position);

        holder.pageNameView.setText(page.getName());
        holder.pageIdView.setText(page.getId());

        holder.itemView.setOnClickListener(v -> pageHandler.onClickFbPage(page));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fbPages.size();
    }
}
