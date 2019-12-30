package com.enonic.xp.script.serializer;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MapGenerator
{
    MapGenerator map();

    MapGenerator map( String key );

    MapGenerator array();

    MapGenerator array( String key );

    MapGenerator value( Object value );

    MapGenerator value( String key, Object value );

    MapGenerator rawValue( Object value );

    MapGenerator rawValue( String key, Object value );

    MapGenerator end();
}
