package com.enonic.wem.api.value;

public abstract class Value<T>
{
    protected final T object;

    protected final ValueType type;

    public Value( final ValueType type, final T object )
    {
        this.type = type;
        this.object = object;

        if ( this.object == null )
        {
            throw new ValueException( "Null value not allowed for [%s]", this.type );
        }
    }

    public final T getObject()
    {
        return this.object;
    }

    public final ValueType getType()
    {
        return this.type;
    }

    public String asString()
    {
        return this.object.toString();
    }

    @Override
    public final String toString()
    {
        return asString();
    }
}
