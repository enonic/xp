package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ApplicationServiceSystemAppGuardsTest
    extends BundleBasedTest
{
    private static final String SYSTEM_APP_NAME = "com.enonic.test.systemapp";

    private ApplicationService applicationService;

    @BeforeEach
    void initService()
    {
        final BundleContext bundleContext = getBundleContext();

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        final NodeService nodeService = mock( NodeService.class );

        final ApplicationFactoryServiceImpl applicationFactoryService =
            new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        applicationFactoryService.activate();

        final ApplicationAuditLogSupportImpl auditLogSupport = new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) );
        auditLogSupport.activate( appConfig );

        this.applicationService = new ApplicationServiceImpl(
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService ),
            mock( ApplicationRepoService.class ), mock( EventPublisher.class ), new AppFilterServiceImpl( appConfig ),
            new VirtualAppService( nodeService ), auditLogSupport );
    }

    @Test
    void deploy_installed_system_app_cannot_be_stopped()
    {
        final ApplicationKey key = ApplicationKey.from( SYSTEM_APP_NAME );

        final Application installed =
            adminContext().callWith( () -> applicationService.installLocalApplication( createSystemBundleSource() ) );

        assertThat( installed ).isNotNull();
        assertThat( installed.isSystem() ).isTrue();

        final Bundle bundle = getBundleContext().getBundle( SYSTEM_APP_NAME );
        assertThat( bundle ).isNotNull();
        assertThat( bundle.getState() ).isEqualTo( Bundle.ACTIVE );

        adminContext().runWith( () -> {
            assertThatThrownBy( () -> applicationService.stopApplication( key ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "system application" );
            assertThat( bundle.getState() ).isEqualTo( Bundle.ACTIVE );
        } );

        assertThat( applicationService.getInstalledApplication( key ) ).isNotNull();
    }

    private static ByteSource createSystemBundleSource()
    {
        try
        {
            return ByteSource.wrap( ByteStreams.toByteArray( TinyBundles.bundle()
                                                                 .setHeader( Constants.BUNDLE_SYMBOLICNAME, SYSTEM_APP_NAME )
                                                                 .setHeader( Constants.BUNDLE_VERSION, "1.0.0" )
                                                                 .setHeader( "X-Bundle-Type", "system" )
                                                                 .addResource( "application.yml", ByteSource.wrap(
                                                                     "kind: \"Application\"\ndescription: \"Test system app\"\n".getBytes(
                                                                         StandardCharsets.UTF_8 ) ).openStream() )
                                                                 .build() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static Context adminContext()
    {
        return ContextBuilder.create()
            .branch( SystemConstants.BRANCH_SYSTEM )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .build();
    }
}
