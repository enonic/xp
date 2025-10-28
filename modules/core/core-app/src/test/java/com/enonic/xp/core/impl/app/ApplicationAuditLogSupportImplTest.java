package com.enonic.xp.core.impl.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApplicationAuditLogSupportImplTest
{
    private ApplicationAuditLogSupportImpl instance;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp()
    {
        this.auditLogService = mock( AuditLogService.class );

        AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        this.instance = new ApplicationAuditLogSupportImpl( auditLogService );
        this.instance.activate( appConfig );
    }

    @Test
    void testStartApplication()
    {
        instance.startApplication( ApplicationKey.from( "com.enonic.app.testApp" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.application.start", value.getType() );
        assertEquals( "com.enonic.app.testApp", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void stopApplication()
    {
        instance.stopApplication( ApplicationKey.from( "com.enonic.app.testApp" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.application.stop", value.getType() );
        assertEquals( "com.enonic.app.testApp", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void installApplication_byURL()
        throws MalformedURLException
    {
        instance.installApplication( ApplicationKey.from( "com.enonic.app.testApp" ), new URL(
            "https://repo.enonic.com/snapshot/com/enonic/app/testApp/2.5.1-SNAPSHOT/testApp-2.5.1-20220301.200937-4.jar?p=v" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.application.install", value.getType() );
        assertEquals( "com.enonic.app.testApp", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
        assertEquals( "https://repo.enonic.com/snapshot/com/enonic/app/testApp/2.5.1-SNAPSHOT/testApp-2.5.1-20220301.200937-4.jar",
                      value.getData().getSet( "params" ).getString( "url" ) );
    }

    @Test
    void installApplication()
    {
        instance.installApplication( ApplicationKey.from( "com.enonic.app.testApp" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.application.install", value.getType() );
        assertEquals( "com.enonic.app.testApp", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void uninstallApplication()
    {
        instance.uninstallApplication( ApplicationKey.from( "com.enonic.app.testApp" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.application.uninstall", value.getType() );
        assertEquals( "com.enonic.app.testApp", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }
}
