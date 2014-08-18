package com.enonic.wem.api.content;

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
