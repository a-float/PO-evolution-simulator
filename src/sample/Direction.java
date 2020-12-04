package sample;

public enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST,
    SOUTH, SOUTHWEST, WEST, NORTHWEST;

    public Vector2 toUnitVector(){
        return switch(this){
            case NORTH     -> new Vector2(0,1);
            case NORTHEAST -> new Vector2(1,1);
            case EAST      -> new Vector2(1,0);
            case SOUTHEAST -> new Vector2(1,-1);
            case SOUTH     -> new Vector2(0,-1);
            case SOUTHWEST -> new Vector2(-1,-1);
            case WEST      -> new Vector2(-1,0);
            case NORTHWEST -> new Vector2(-1,1);
        };
    }
    public Direction rotateBy(int rotation){
        int len = Direction.values().length;
        //Math.floorMod is needed because in java % is a remainder, not python like modulus
        return Direction.values()[Math.floorMod(this.ordinal()+rotation, len)];
    }

    public static Direction fromInt(int dirIndex) throws ArrayIndexOutOfBoundsException{
        try {
           return Direction.values()[dirIndex];
        }
        catch (Exception e) {
            String message = String.format("Tried to access element at index %d",dirIndex);
            throw new ArrayIndexOutOfBoundsException(message);
        }
    }
}
