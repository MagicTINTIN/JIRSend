package com.JIRSend.model.user;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.model.user.UserEntry.Status;

public class UserEntryTest {
    @Test
    void testToString() {
        String username = "someone";
        UserEntry ue = new UserEntry(Status.Offline, username);
        assertTrue(ue.toString().equals(username + " (OFFLINE)"));
    }
}
