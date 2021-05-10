package com.gazitf.etapp.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.RecyclerViewItemRequestBinding;
import com.gazitf.etapp.repository.FirestoreDbConstants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/*
 * @created 22/03/2021 - 5:54 PM
 * @project EtApp
 * @author fnkaya
 */
public class RequestListRecyclerViewAdapter extends RecyclerView.Adapter<RequestListRecyclerViewAdapter.RequestViewHolder> {

    private Context context;
    private List<DocumentSnapshot> documentSnapshotList;
    private RequestPostClickListener requestPostClickListener;

    public RequestListRecyclerViewAdapter(Context context,List<DocumentSnapshot> documentSnapshotList, RequestPostClickListener requestPostClickListener) {
        this.context = context;
        this.documentSnapshotList = documentSnapshotList;
        this.requestPostClickListener = requestPostClickListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewItemRequestBinding binding = RecyclerViewItemRequestBinding.inflate(inflater, parent, false);

        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        DocumentSnapshot requestSnapshot = documentSnapshotList.get(position);

        Map<String, Object> requestData = requestSnapshot.getData();
        String requestMessage = (String) requestData.get(FirestoreDbConstants.RequestConstants.REQUEST_MESSAGE);
        String activityId = (String) requestData.get(FirestoreDbConstants.RequestConstants.ACTIVITY_ID);
        holder.textViewOwnerMessage.setText(context.getResources().getString(R.string.request_message, requestMessage));

        String requestOwnerId = (String) requestSnapshot.get(FirestoreDbConstants.RequestConstants.OWNER_ID);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection(FirestoreDbConstants.UsersConstants.COLLECTION)
                .document(requestOwnerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        String displayName = (String) userData.get(FirestoreDbConstants.UsersConstants.DISPLAY_NAME);
                        String photoUrl = (String) userData.get(FirestoreDbConstants.UsersConstants.PHOTO_URL);

                        holder.textViewOwnerName.setText(displayName);
                        Picasso.get()
                                .load(photoUrl)
                                .into(holder.imageViewOwnerImage);
                    }
                });

        firestore
                .collection(FirestoreDbConstants.ActivitiesConstants.COLLECTION)
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String activityName = (String) documentSnapshot
                                .get(FirestoreDbConstants.ActivitiesConstants.ACTIVITY_NAME);
                        holder.textViewActivityName.setText(activityName);
                    }
                });

        holder.buttonAccept.setOnClickListener(v -> requestPostClickListener.acceptRequest(activityId, requestOwnerId));
        holder.buttonReject.setOnClickListener(v -> {
            requestPostClickListener.rejectRequest(activityId, requestOwnerId);
        });
    }

    @Override
    public int getItemCount() {
        return documentSnapshotList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewOwnerImage;
        private TextView textViewOwnerName, textViewOwnerMessage, textViewActivityName;
        private ImageButton buttonAccept, buttonReject;

        public RequestViewHolder(@NonNull RecyclerViewItemRequestBinding binding) {
            super(binding.getRoot());

            imageViewOwnerImage = binding.imageViewRequestOwnerImage;
            textViewOwnerName = binding.textViewRequestOwnerName;
            textViewOwnerMessage = binding.textViewRequestOwnerMessage;
            textViewActivityName = binding.textViewActivityName;
            buttonAccept = binding.buttonRequestAccept;
            buttonReject = binding.buttonRequestReject;
        }
    }

    public interface RequestPostClickListener {
        void acceptRequest(String activityId, String requestOwnerId);
        void rejectRequest(String activityId, String requestOwnerId);
    }
}
