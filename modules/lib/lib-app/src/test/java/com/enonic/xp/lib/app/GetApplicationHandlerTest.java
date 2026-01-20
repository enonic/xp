package com.enonic.xp.lib.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.Version;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetApplicationHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    void testExample()
    {

        when( applicationService.get( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final Application application = mock( Application.class );

            when( application.getKey() ).thenReturn( applicationKey );
            when( application.getDisplayName() ).thenReturn( "app display name" );
            when( application.getVendorName() ).thenReturn( "vendor name" );
            when( application.getVendorUrl() ).thenReturn( "https://vendor.url" );
            when( application.getUrl() ).thenReturn( "https://myapp.url" );
            when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
            when( application.getSystemVersion() ).thenReturn( ( "4.2.3-SNAPSHOT" ) );
            when( application.getMinSystemVersion() ).thenReturn( "2.0.0" );
            when( application.getMaxSystemVersion() ).thenReturn( "3.0.0" );
            when( application.getModifiedTime() ).thenReturn( Instant.parse( "2020-09-25T10:00:00.00Z" ) );
            when( application.isStarted() ).thenReturn( true );
            when( application.isSystem() ).thenReturn( true );

            return application;
        } );

        runScript( "/lib/xp/examples/app/get.js" );
    }

    @Test
    void testMissing()
    {

        when( applicationService.get( isA( ApplicationKey.class ) ) ).thenAnswer( params -> null );

        runFunction( "/test/GetApplicationHandlerTest.js", "getMissing" );
    }
}
