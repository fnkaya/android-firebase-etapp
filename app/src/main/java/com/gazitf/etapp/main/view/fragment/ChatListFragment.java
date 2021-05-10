package com.gazitf.etapp.main.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gazitf.etapp.databinding.FragmentChatListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.Member;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

public class ChatListFragment extends Fragment {

    private FirebaseUser currentUser;
    private final String apiKey = "v3de648822hg";

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
    }

    private void setUpChatList() {
        ChatClient chatClient = ChatClient.instance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User();
        user.setId(currentUser.getUid());
        user.getExtraData().put("name", currentUser.getDisplayName());
        Uri photoUrl = currentUser.getPhotoUrl();
        if (photoUrl != null)
            user.getExtraData().put("image", photoUrl.toString());

        chatClient
                .connectUser(user, chatClient.devToken(currentUser.getUid()))
                .enqueue();

        FilterObject filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.in("members", Collections.singletonList(chatClient.getCurrentUser().getId()))
        );

        ChannelListViewModelFactory factory = new ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT);
        ChannelListViewModel channelListViewModel = new ViewModelProvider(this, factory).get(ChannelListViewModel.class);
        ChannelListViewModelBinding.bind(channelListViewModel, binding.channelListView, this);

        binding.channelListView.setChannelItemClickListener(channel -> {
            startActivity(ChannelActivity.newIntent(requireActivity(), channel));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}