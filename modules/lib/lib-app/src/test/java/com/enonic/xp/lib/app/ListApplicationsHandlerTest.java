package com.enonic.xp.lib.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.util.Version;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListApplicationsHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    void testExample()
    {

        when( applicationService.getInstalledApplications() ).thenAnswer( params -> {
            final Application application1 = mock( Application.class );

            when( application1.getKey() ).thenReturn( ApplicationKey.from( "app1" ) );
            when( application1.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
            when( application1.isStarted() ).thenReturn( true );
            when( application1.getMinSystemVersion() ).thenReturn( "2.0.0" );
            when( application1.getMaxSystemVersion() ).thenReturn( "3.0.0" );
            when( application1.getSystemVersion() ).thenReturn( "1.21.3" );
            when( application1.getModifiedTime() ).thenReturn( Instant.parse( "2020-09-25T10:00:00.00Z" ) );

            final Application application2 = mock( Application.class );

            when( application2.getKey() ).thenReturn( ApplicationKey.from( "app2" ) );
            when( application2.getVersion() ).thenReturn( Version.parseVersion( "4.1.2" ) );
            when( application2.getSystemVersion() ).thenReturn( "1.2.33-SNAPSHOT" );
            when( application2.getMinSystemVersion() ).thenReturn( "5.3.11" );
            when( application2.getMaxSystemVersion() ).thenReturn( "3.0.6" );
            when( application2.getModifiedTime() ).thenReturn( Instant.parse( "2021-09-25T10:00:00.00Z" ) );
            when( application2.isSystem() ).thenReturn( true );

            return Applications.from( application1, application2 );
        } );

        runScript( "/lib/xp/examples/app/list.js" );
    }
}
