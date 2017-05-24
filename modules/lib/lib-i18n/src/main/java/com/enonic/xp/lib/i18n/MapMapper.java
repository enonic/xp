package com.enonic.xp.lib.i18n;

import java.util.Map;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

final class MapMapper
    implements MapSerializable
{
    private final Map<String, String> map;

    MapMapper( final Map<String, String> map )
    {
        this.map = map;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( final Map.Entry<String, String> entry : this.map.entrySet() )
        {
            gen.value( entry.getKey(), entry.getValue() );
        }
    }
}
