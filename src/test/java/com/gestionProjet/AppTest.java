package com.gestionProjet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    @Test
    void testMain() {
        assertEquals(1,1);
    }

    @Test
    void testAdd()
    {
        assertEquals(App.add(2,3),5);
    }
}