package com.enonic.xp.core.impl.media;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MediaTypesLoaderTest
{
    private MediaTypesLoader loader;

    @BeforeEach
    public void setup()
    {
        this.loader = new MediaTypesLoader();
    }

    @Test
    public void testActivate()
    {
        final Map<String, String> config = Maps.newHashMap();
        config.put( "a", "b" );
        config.put( "ext.test", "other/unknown" );
        config.put( "ext.txt", "text/plain" );

        this.loader.activate( config );

        assertEquals( "other/unknown", this.loader.fromExt( "test" ).toString() );
        assertEquals( "text/plain", this.loader.fromExt( "txt" ).toString() );
        assertEquals( "{txt=text/plain, test=other/unknown}", this.loader.asMap().toString() );
    }
}
