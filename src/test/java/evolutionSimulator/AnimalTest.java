package evolutionSimulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    Animal anim1;
    Animal anim2;

    @BeforeEach
    void setUp() {
        anim1 = new Animal(Vector2.zero(), new Genome(15), 30);
        anim2 = new Animal(Vector2.zero(), new Genome(15), 0);
        anim1.currDirection=Direction.NORTHWEST;
        anim2.currDirection=Direction.EAST;
    }

    @Test
    void getColor() {
        assertNotNull(anim1.getColor());
        assertNotNull(anim2.getColor());
    }

    @Test
    void deliverBaby() {
    }

    @Test
    void getNewRawPosition() {
        assertEquals(new Vector2(-1,-1), anim1.getNewRawPosition());
    }

    @Test
    void eat() {
        anim1.eat(20);
        assertEquals(50, anim1.energy);
    }

    @Test
    void testToString() {
        assertEquals("Animal (pos = "+anim1.position+" energy = 30, dir = NORTHWEST)", anim1.toString());
        assertEquals("Animal (pos = "+anim2.position+" energy = 0, dir = EAST)", anim2.toString());
    }

    @Test
    void toShortString() {
        assertEquals("Animal [energy=30, genome="+anim1.genome+" dir=NORTHWEST]", anim1.toShortString());
        assertEquals("Animal [energy=0, genome=" +anim2.genome+" dir=EAST]", anim2.toShortString());
    }
}