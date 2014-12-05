package com.enonic.wem.script.serializer;

// TODO: Rename to MapBuilder, MapWriter or something else?
public interface MapGenerator
{
    MapGenerator map();

    MapGenerator map( String key );

    MapGenerator array();

    MapGenerator array( String key );

    MapGenerator value( Object value );

    MapGenerator value( String key, Object value );

    MapGenerator end();
}
