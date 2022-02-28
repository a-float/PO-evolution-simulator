package me.evolutionSimulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import me.utils.Vector2;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class Vector2Test {
    Vector2 vec1;
    Vector2 vec2;

    @BeforeEach
    void setUp() {
        vec1 = new Vector2(0,3);
        vec2 = new Vector2(1,-1);;
    }

    @Test
    void add() {
        vec1.add(vec2);
        assertEquals(new Vector2(1,2), vec1);
        vec2.add(new Vector2(100,0));
        assertEquals(new Vector2(101,-1), vec2);
    }

    @Test
    void testAdd() {
        assertEquals(new Vector2(1,2), Vector2.add(vec1,vec2));
        assertEquals(new Vector2(102,0), Vector2.add(vec2, new Vector2(101,1)));
    }

    @Test
    void subtract() {
        vec1.subtract(vec2);
        assertEquals(new Vector2(-1,4), vec1);
        vec2.subtract(new Vector2(100,0));
        assertEquals(new Vector2(-99,-1), vec2);
    }

    @Test
    void testSubtract() {
        assertEquals(new Vector2(-1,4), Vector2.subtract(vec1,vec2));
        assertEquals(new Vector2(-100,-2), Vector2.subtract(vec2, new Vector2(101,1)));
    }

    @Test
    void opposite() {
        assertEquals(new Vector2(-1,4), new Vector2(1,-4).opposite());
        assertEquals(new Vector2(0,0), new Vector2(0,0).opposite());
        assertNotEquals(new Vector2(0,1), new Vector2(0,1).opposite());
    }

    @Test
    void getAdjacentPositions() {
        Vector2[] adjacent = new Vector2[]{
                    new Vector2(-1, -1), new Vector2(0, -1), new Vector2(1, -1),
                    new Vector2(-1, 0),                            new Vector2(1, 0),
                    new Vector2(-1, 1),  new Vector2(0, 1),   new Vector2(1, 1)
        };
        HashSet<Vector2> adjacantSet = new HashSet<>(Arrays.asList(adjacent));
        System.out.println("Expected adjacent:");
        for(int i = 0; i < 8; i++) System.out.print(adjacent[i]);
        System.out.println();
        Vector2[] zeroAdjacent = Vector2.zero().getAdjacentPositions();
        System.out.println("Actual adjacent:");
        for(int i = 0; i < 8; i++) System.out.print(zeroAdjacent[i]);
        System.out.println();
        for(int i = 0; i < 8; i++){
            assertTrue(adjacantSet.contains(zeroAdjacent[i]));
        }
    }

    @Test
    void zero() {
        assertEquals(new Vector2(0,0), Vector2.zero());
    }

    @Test
    void testEquals() {
        assertEquals(new Vector2(1,1), new Vector2(1,1));
        assertNotEquals(new Vector2(-1,1), new Vector2(1,1));
        assertNotEquals(new Vector2(-3,-3), new Vector2(3,3));
        assertNotEquals(new Vector2(-5,2), new Vector2(2,-5));
        assertNotEquals(new Vector2(-1000,1000), new Vector2(1000,1000));
    }

    @Test
    void testToString() {
        assertEquals("(2,3)", new Vector2(2,3).toString());
        assertEquals("(0,0)", new Vector2(0,0).toString());
        assertEquals("(-1,0)", new Vector2(-1,0).toString());
        assertEquals("(1000,5)", new Vector2(1000,5).toString());
    }
}