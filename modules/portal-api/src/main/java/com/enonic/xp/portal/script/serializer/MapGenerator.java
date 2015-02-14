package com.enonic.xp.portal.script.serializer;

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
