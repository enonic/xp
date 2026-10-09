package com.enonic.xp.site;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteConfigsTest
{
    @Test
    void testApplicationKeys()
    {
        final SiteConfigs siteConfigs = SiteConfigs.from( siteConfig( "app2" ), siteConfig( "app1" ) );

        assertEquals( List.of( ApplicationKey.from( "app2" ), ApplicationKey.from( "app1" ) ),
                      siteConfigs.getApplicationKeys().stream().toList() );
        assertSame( siteConfigs.getApplicationKeys(), siteConfigs.getApplicationKeys() );
    }

    @Test
    void testApplicationKeysOfEmpty()
    {
        assertTrue( SiteConfigs.empty().getApplicationKeys().isEmpty() );
    }

    private static SiteConfig siteConfig( final String application )
    {
        return SiteConfig.create().application( ApplicationKey.from( application ) ).config( new PropertyTree() ).build();
    }
}
