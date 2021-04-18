package com.chat_tracker.Interface;

import com.chat_tracker.Model.User;

public interface UserInterface {
    void privateMessages(User model);
    void directChat(User model);
    void add(User model);
}
