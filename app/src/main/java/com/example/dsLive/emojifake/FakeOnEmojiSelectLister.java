package com.example.dsLive.emojifake;

import com.example.dsLive.databinding.ItemEmojiGridBinding;
import com.example.dsLive.modelclass.FakeGiftRoot;


public interface FakeOnEmojiSelectLister {
    void onEmojiSelect(ItemEmojiGridBinding binding, FakeGiftRoot giftRoot, String giftCount);
}
