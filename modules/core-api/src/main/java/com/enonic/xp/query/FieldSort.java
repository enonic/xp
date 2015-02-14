package com.enonic.xp.query;

public class FieldSort
{
    private final String fieldName;

    private final Direction direction;

    public FieldSort( final String fieldName, final Direction direction )
    {
        this.fieldName = fieldName;
        this.direction = direction;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public Direction getDirection()
    {
        return direction;
    }
}
