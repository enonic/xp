package com.enonic.wem.script.serializer;

public interface MapGenerator
{
    public MapGenerator map();

    public MapGenerator array();

    public MapGenerator end();

    public MapGenerator name( String name );

    public MapGenerator value( Object value );
}
