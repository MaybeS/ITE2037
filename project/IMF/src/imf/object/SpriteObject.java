package imf.object;

import imf.data.DataObject;
import loot.graphics.DrawableObject3D;
import loot.graphics.VisualObject3D;

public class SpriteObject extends DrawableObject3D
{
	public String name, ID, type, texture;
	public boolean collision;
	public int interval;
	public double box_top = 0, box_bottom = 0, box_left = 0, box_right = 0;

	public SpriteObject(double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
		box_top = y + this.radius_y;
		box_bottom = y - this.radius_y;
		box_left = x - this.radius_x;
		box_right = x + this.radius_x;
	}
	public SpriteObject(DataObject o)
	{
		this(Integer.valueOf(o.get("x")), Integer.valueOf(o.get("y")), Integer.valueOf(o.get("z")), Integer.valueOf(o.get("w")), Integer.valueOf(o.get("h")));
		this.collision = o.get("collision").equals("true");
		this.interval = Integer.valueOf(o.get("interval"));
		this.pos_z = Integer.valueOf(o.get("z"));
		this.texture = o.get("texture");
		this.type = o.get("type");
		this.name = o.get("name");
		this.ID = o.ID;
	}
	
	public void visible(boolean value)
	{
		trigger_hide = value;
	}
	
	public void setPositionAbove(SpriteObject o)
	{
		pos_y = o.box_top + radius_y;
	}
	public void setPositionBelow(SpriteObject o)
	{
		pos_y = o.box_bottom - radius_y;
	}
	public void setPositionLeft(SpriteObject o)
	{
		pos_x = o.box_right + radius_x;
	}
	public void setPositionRight(SpriteObject o)
	{
		pos_x = o.box_left - radius_x;
	}
	
	/**
	 * left:	 x,
	 * in:		 0,
	 * right:	-x
	 * @param o
	 * @return relative X position to o
	 */
	public double relativeX(VisualObject3D o)
	{
		return Math.abs(distanceX(o)) < (this.radius_x + o.radius_x) ? 0 : distanceX(o);
	}

	/**
	 * up:	 	 x,
	 * in:	 	 0,
	 * down:	-x
	 * @param o
	 * @return relative Y position to o
	 */
	public double relativeY(VisualObject3D o)
	{
		return Math.abs(distanceY(o)) < (this.radius_y + o.radius_y) ? 0 : distanceY(o);
	}
	
	/**
	 * @param o
	 * @return distance X position to o
	 */
	public double distanceX(VisualObject3D o)
	{
		return (this.pos_x - o.pos_x);
	}
	
	/**
	 * @param o
	 * @return distance Y position to o
	 */
	public double distanceY(VisualObject3D o)
	{
		return (this.pos_y - o.pos_y);
	}
	
	/**
	 * @param o
	 * @return aperture X position to o
	 */
	public double apertureX(VisualObject3D o)
	{
		return Math.abs(distanceX(o)) - (this.radius_x + o.radius_x);
	}
	
	/**
	 * @param o
	 * @return aperture Y position to o
	 */
	public double apertureY(VisualObject3D o)
	{
		return Math.abs(distanceY(o)) - (this.radius_y + o.radius_y);
	}
	
	/**
	 * @param o
	 * @return o is in same z position
	 */
	public boolean zPosition(VisualObject3D o)
	{
		return this.pos_z == o.pos_z;
	}
}