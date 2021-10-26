package jgame;

public enum Direction {
	/**Degrees: 0
	 * <br>Radians: 0
	 */
	EAST(0,0),
	/**Degrees: 45
	 * <br>Radians: π/4
	 */
	SOUTHEAST(45,Math.PI/4),
	/**Degrees: 90
	 * <br>Radians: π/2
	 */
	SOUTH(90,Math.PI/2),
	/**Degrees: 135
	 * <br>Radians: 3π/4
	 */
	SOUTHWEST(135,Math.PI*3/4),
	/**Degrees: 180
	 * <br>Radians: π
	 */
	WEST(180,Math.PI),
	/**Degrees: -135
	 * <br>Radians: -3π/4
	 */
	NORTHWEST(-135,-Math.PI*3/4),
	/**Degrees: -90
	 * <br>Radians: -π/2
	 */
	NORTH(-90,-Math.PI/2),
	/**Degrees: -45
	 * <br>Radians: -π/4
	 */
	NORTHEAST(-45,-Math.PI/4);
	
	/** The value, in degrees, of this Direction*/
	public final int degrees;
	
	/** The value, in radians, of this Direction*/
	public final double radians;
	
	Direction(int degrees, double radians) {
		this.degrees = degrees;
		this.radians = radians;
	}
	
	/**
	 * Returns the {@linkplain Direction} which is opposite (rotated 180 degrees) this one
	 * @return the opposite direction
	 */
	public Direction opposite() {
		return switch (this) {
			case EAST		-> WEST;
			case SOUTHEAST	-> NORTHWEST;
			case SOUTH		-> NORTH;
			case SOUTHWEST	-> NORTHEAST;
			case WEST		-> EAST;
			case NORTHWEST	-> SOUTHEAST;
			case NORTH		-> SOUTH;
			case NORTHEAST	-> SOUTHWEST;
		};
	}
	
	/**
	 * Returns an array of the four cardinal directions: North, South, East, and West
	 * @return Direction[] {NORTH, SOUTH, EAST, WEST}
	 */
	public static Direction[] cardinals() {
		return new Direction[] {NORTH, SOUTH, EAST, WEST};
	}
	
	/**
	 * Returns an array of the four ordinal directions: Northeast, Northwest, Southeast, and Southwest
	 * @return Direction[] {NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST}
	 */
	public static Direction[] ordinals() {
		return new Direction[] {NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST};
	}
	
	/**
	 * Returns the angle equivalent to {@code degrees} within the range {@code (-180,180]}
	 * @param degrees the angle to constrain
	 * @return the constrained angle
	 */
	public static double normalizeDegrees(double degrees) {
		if(degrees > -180 && degrees <= 180) {
			return degrees;
		}
		degrees %= 360;
		if(degrees <= -180) {
			degrees += 360;
		} else if(degrees > 180) {
			degrees -= 360;
		}
		return degrees;
	}
	
	/**
	 * Returns the angle equivalent to {@code radians} within the range {@code (-π,π]}
	 * @param radians the angle to constrain
	 * @return the constrained angle
	 */
	public static double normalizeRadians(double radians) {
		if(radians > -Math.PI && radians <= Math.PI) {
			return radians;
		}
		radians %= 2*Math.PI;
		if(radians <= -Math.PI) {
			radians += 2*Math.PI;
		} else if(radians > Math.PI) {
			radians -= 2*Math.PI;
		}
		return radians;
	}
}
