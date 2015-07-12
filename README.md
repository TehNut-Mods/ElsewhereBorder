#Elsewhere Border

A simple, easy to use World Border mod for Forge.

##Default Config
```
# Configuration file

general {
    # Dimension specific boundaries.
    # Syntax is DimensionID:XxZ
    # DimensionID = ID of the dimension you're specifying a bound for. IE: 0 for Overworld
    # X = Maximum X Coordinate a player can travel to. This also goes negative. IE: 1000 will allow the player to travel between 1000 and -1000 on the X axis
    # Z = Maximum Z Coordinate a player can travel to. This also goes negative. IE: 1000 will allow the player to travel between 1000 and -1000 on the Z axis [default: [0:2000x2000], [1:2000x2000], [-1:250x250]]
    S:dimBounds <
        0:2000x2000
        1:2000x2000
        -1:250x250
     >

    # Message to send players when they have crossed the border. [default: Out of bounds, soldier!]
    S:outOfBoundsMessage=Out of bounds, soldier!
}
```
