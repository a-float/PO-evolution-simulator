package evolutionSimulator;

import java.util.Random;

public enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST,
    SOUTH, SOUTHWEST, WEST, NORTHWEST;

    /**
     * Y-axis is inverted, because the drawing is done with the Y-axis poiting downwards
     * @return Vector2 pointing in specified direction inverted on the Y-axis
     */
    public Vector2 toUnitVector(){
        return switch(this){
            case NORTH     -> new Vector2(0,-1);
            case NORTHEAST -> new Vector2(1,-1);
            case EAST      -> new Vector2(1,0);
            case SOUTHEAST -> new Vector2(1,1);
            case SOUTH     -> new Vector2(0,1);
            case SOUTHWEST -> new Vector2(-1,1);
            case WEST      -> new Vector2(-1,0);
            case NORTHWEST -> new Vector2(-1,-1);
        };
    }

    /**
     * @param rotation angle is rotation * 45deg
     * @return a direction which is rotated by rotation * 45deg from the give one
     */
    public Direction rotateBy(int rotation){
        int len = Direction.values().length;
        //Math.floorMod is needed because in java % is a remainder, not python like modulus
        return Direction.values()[Math.floorMod(this.ordinal()+rotation, len)];
    }

    /**
     * @param dirIndex direction index
     * @return  Direction represented by the dirIndex
     * @throws ArrayIndexOutOfBoundsException if the index is not in range [0,7]
     */
    public static Direction fromInt(int dirIndex) throws ArrayIndexOutOfBoundsException{
        try {
           return Direction.values()[dirIndex];
        }
        catch (Exception e) {
            String message = String.format("Tried to access element at index %d",dirIndex);
            throw new ArrayIndexOutOfBoundsException(message);
        }
    }

    /**
     * @return a random enum value
     */
    public static Direction getRandomDirection(){
        return Direction.values()[new Random().nextInt(Direction.values().length)];
    }
}
