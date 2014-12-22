package com.enonic.wem.script.serializer;

public interface MapGenerator2
{
    public Object getModel();

    public MapGenerator2 field( String name );

    public MapGenerator2 object();

    public MapGenerator2 array();

    public MapGenerator2 value( Object value );

    public MapGenerator2 end();
}
