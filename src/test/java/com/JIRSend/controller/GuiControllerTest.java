package com.JIRSend.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.SocketException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import com.JIRSend.model.network.Net;

@Tag("Controller")
@ResourceLock("NETWORK_RESSOURCE")
public class GuiControllerTest {
    static MainController controller;
    
    @BeforeAll
    static void setup() throws SocketException {
        controller = new MainController(true);
        assertDoesNotThrow(() -> controller.startUI());
    }

    @AfterAll
    static void cleanup() throws InterruptedException {
        // controller.stoppingApp();
        controller.stopNet();
        Thread.sleep(100);
    }

    @Test
    void testChangeUsername() {
        assertEquals(Net.okString, controller.changeUsername("ShouldBeOK"));
        assertEquals("ShouldBeOK", controller.getUsername());
    }

    @Test
    void testGetConnectedUsernames() {
        assertTrue(controller.getConnectedUsernames().isEmpty());
    }

    @Test
    void testGetContacts() {
        assertTrue(controller.getContacts().isEmpty());
    }

    @Test
    void testGetConversation() {
        assertEquals(null, controller.getConversation());
        assertEquals(null, controller.getConversation("doesntexist"));
    }

    @Test
    void testGetConversationIP() {
        assertEquals(null, controller.getConversationIP());
        assertEquals(null, controller.getConversationIP("doesntexist"));
    }

    @Test
    void testGetConversationName() {
        assertEquals(null, controller.getConversationName());
    }

    @Test
    void testGetConversationUnreadNumber() {
        assertEquals(0, controller.getConversationUnreadNumber("doesntexist"));
    }

    @Test
    void testGetIPFromUsername() {
        assertEquals(null, controller.getIPFromUsername("doesntexist"));
    }

    @Test
    void testGetName() {
        assertEquals("JIRSend Main", controller.getName());
    }

    @Test
    void testGetNumberConnected() {
        assertEquals(0, controller.getNumberConnected());
    }

    @Test
    void testGetTotalUnread() {
        assertEquals(0, controller.getTotalUnread());
    }

    @Test
    void testGetUsernameFromIP() {
        assertEquals(null, controller.getUsernameFromIP("noIP"));
    }

    @Test
    void testIsConnected() {
        assertFalse(controller.isConnected("doesntexist"));
    }

    @Test
    void testMarkConversationRead() {
        assertDoesNotThrow(() -> controller.markConversationRead("doesntexist"));
    }
}
