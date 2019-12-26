package com.enonic.xp.script.serializer;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MapSerializable
{
    void serialize( MapGenerator gen );
}
