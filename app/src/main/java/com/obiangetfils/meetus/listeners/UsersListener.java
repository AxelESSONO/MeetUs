package com.obiangetfils.meetus.listeners;

import com.obiangetfils.meetus.models.User;

public interface UsersListener {

    void initiateVideoMeeting(User user);
    void initiateAudioMeeting(User user);

}
