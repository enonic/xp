package com.enonic.xp.script.serializer;

import com.google.common.annotations.Beta;

@Beta
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
