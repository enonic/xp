package com.enonic.xp.impl.server.rest;

import org.junit.jupiter.api.Test;

import com.enonic.xp.util.ByteSizeParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppManagementConfigTest
{
    private static AppManagementConfig config()
    {
        return mock( AppManagementConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
    }

    @Test
    void defaults()
    {
        final AppManagementConfig config = config();
        assertEquals( "https://*", ApplicationResourceService.allowedUrls( config ) );
        assertTrue( ApplicationResourceService.checksumRequired( config ) );
        assertEquals( ByteSizeParser.parse( "1gb" ), ApplicationResourceService.maxSize( config ) );
        assertEquals( 5, ApplicationResourceService.maxRedirects( config ) );
        assertEquals( 10_000, ApplicationResourceService.connectTimeoutMillis( config ) );
        assertEquals( 60_000, ApplicationResourceService.readTimeoutMillis( config ) );
    }

    @Test
    void legacyKeys()
    {
        final AppManagementConfig config = config();
        when( config.installUrl_allowedUrls() ).thenReturn( "https://legacy/*" );
        when( config.installUrl_checksumRequired() ).thenReturn( false );
        assertEquals( "https://legacy/*", ApplicationResourceService.allowedUrls( config ) );
        assertFalse( ApplicationResourceService.checksumRequired( config ) );
    }

    @Test
    void pullKeysWinOverLegacy()
    {
        final AppManagementConfig config = config();
        when( config.pull_allowedUrls() ).thenReturn( "https://new/*" );
        when( config.installUrl_allowedUrls() ).thenReturn( "https://legacy/*" );
        when( config.pull_checksumRequired() ).thenReturn( " true " );
        when( config.installUrl_checksumRequired() ).thenReturn( false );
        assertEquals( "https://new/*", ApplicationResourceService.allowedUrls( config ) );
        assertTrue( ApplicationResourceService.checksumRequired( config ) );
    }

    @Test
    void emptyPullAllowedUrlsDisables()
    {
        final AppManagementConfig config = config();
        when( config.pull_allowedUrls() ).thenReturn( "" );
        when( config.installUrl_allowedUrls() ).thenReturn( "https://legacy/*" );
        assertEquals( "", ApplicationResourceService.allowedUrls( config ) );
    }

    @Test
    void pullLimitsAreConfigurable()
    {
        final AppManagementConfig config = config();
        when( config.pull_maxSize() ).thenReturn( "512mb" );
        when( config.pull_maxRedirects() ).thenReturn( 9 );
        when( config.pull_connectTimeout() ).thenReturn( "PT3S" );
        when( config.pull_readTimeout() ).thenReturn( "PT45S" );

        assertEquals( ByteSizeParser.parse( "512mb" ), ApplicationResourceService.maxSize( config ) );
        assertEquals( 9, ApplicationResourceService.maxRedirects( config ) );
        assertEquals( 3_000, ApplicationResourceService.connectTimeoutMillis( config ) );
        assertEquals( 45_000, ApplicationResourceService.readTimeoutMillis( config ) );
    }
}
