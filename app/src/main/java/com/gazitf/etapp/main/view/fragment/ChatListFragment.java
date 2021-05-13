package com.gazitf.etapp.main.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gazitf.etapp.R;
import com.gazitf.etapp.databinding.FragmentChatListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.Member;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel;
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

public class ChatListFragment extends Fragment {

    private FragmentChatListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);

        setUpChatList();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.channelListView.setChannelDeleteClickListener(this::showDeleteChannelDialog);

        binding.channelListView.setChannelItemClickListener(channel -> {
            startActivity(ChannelActivity.newIntent(requireActivity(), channel));
        });
    }

    private void setUpChatList() {
        ChatClient chatClient = ChatClient.instance();

        FilterObject filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.in("members", Collections.singletonList(chatClient.getCurrentUser().getId()))
        );

        ChannelListViewModelFactory factory = new ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT);
        ChannelListViewModel channelListViewModel = new ViewModelProvider(this, factory).get(ChannelListViewModel.class);
        ChannelListViewModelBinding.bind(channelListViewModel, binding.channelListView, getViewLifecycleOwner());
    }

    private void showDeleteChannelDialog(Channel channel){
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(channel.getExtraData().get("name").toString())
                .setMessage("Sohbeti silmek istiyor musunuz?")
                .setIcon(R.drawable.icon_delete_forever)
                .setCancelable(false)
                .setNegativeButton("VAZGEÇ", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("SİL", (dialog, which) -> deleteChannel(channel))
                .show();
    }

    private void deleteChannel(Channel channel) {
        ChatDomain.instance()
                .getUseCases()
                .getDeleteChannel()
                .invoke(channel.getCid())
                .enqueue(result -> {
                    if (result.isSuccess())
                        Toast.makeText(requireActivity(), channel.getExtraData().get("name") + " kanalı silindi", Toast.LENGTH_LONG).show();
                    else
                        Log.i("TAG", "deleteChannel: " + result.error().getMessage());
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}