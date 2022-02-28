package me.evolutionSimulator;

import me.evolutionSimulator.Direction;
import org.junit.jupiter.api.Test;
import me.utils.Vector2;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test
    void toUnitVector() {
        assertEquals(new Vector2(1,-1), Direction.NORTHEAST.toUnitVector());
        assertEquals(new Vector2(0,1), Direction.SOUTH.toUnitVector());
        assertEquals(new Vector2(-1,-1), Direction.NORTHWEST.toUnitVector());
        assertEquals(new Vector2(-1,1), Direction.SOUTHWEST.toUnitVector());
    }

    @Test
    void rotateBy() {
        assertEquals(Direction.NORTH, Direction.NORTH.rotateBy(0));
        assertEquals(Direction.NORTHWEST, Direction.NORTH.rotateBy(-1));
        assertEquals(Direction.SOUTH, Direction.NORTH.rotateBy(4));
        assertEquals(Direction.NORTH, Direction.NORTH.rotateBy(8));
    }

    @Test
    void fromInt() {
        assertEquals(Direction.NORTH, Direction.fromInt(0));
        assertEquals(Direction.SOUTH, Direction.fromInt(4));
        assertEquals(Direction.WEST, Direction.fromInt(6));
        assertEquals(Direction.NORTH, Direction.fromInt(0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Direction.fromInt(-1));

    }
}