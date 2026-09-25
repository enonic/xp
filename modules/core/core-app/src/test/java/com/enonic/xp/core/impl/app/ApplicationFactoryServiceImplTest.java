package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationFactoryServiceImplTest
    extends BundleBasedTest
{
    @Mock(stubOnly = true)
    private NodeService nodeService;

    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertNotNull( service.getApplication( bundle ) );

        bundle.start();
        assertNotNull( service.getApplication( bundle ) );

        bundle.stop();
        assertNotNull( service.getApplication( bundle ) );

        service.deactivate();

        assertNull( service.getApplication( bundle ) );
    }

    @Test
    void findActiveApplication()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();

        bundle.start();
        assertThat( service.findActiveApplication( applicationKey ) ).isNotEmpty();

        bundle.stop();
        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();
    }

    @Test
    void findActiveResolver()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertThat( service.findResolver( applicationKey, null ) ).isEmpty();

        bundle.start();
        Optional<ApplicationUrlResolver> activeResolver = service.findResolver( applicationKey, null );
        assertThat( activeResolver ).isNotEmpty();
        assertThat( activeResolver.get() ).isInstanceOf( BundleApplicationUrlResolver.class );

        activeResolver = service.findResolver( applicationKey, "bundle" );
        assertThat( activeResolver ).isNotEmpty();
        assertThat( activeResolver.get() ).isInstanceOf( BundleApplicationUrlResolver.class );

        bundle.stop();
        assertThat( service.findResolver( applicationKey, null ) ).isEmpty();
    }

    @Test
    void findResolver_inactiveWithPersistedSchema()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) );

        // installed but not started: the persisted schema is served
        assertThat( service.findResolver( applicationKey, null ) ).containsInstanceOf( NodeResourceApplicationUrlResolver.class );
        assertThat( service.findResolver( applicationKey, "bundle" ) ).isEmpty();

        bundle.start();
        assertThat( service.findResolver( applicationKey, null ) ).isNotEmpty()
            .get()
            .isNotInstanceOf( NodeResourceApplicationUrlResolver.class );

        // stopped: the persisted schema is still served, bundle resources are not
        bundle.stop();
        assertThat( service.findResolver( applicationKey, null ) ).containsInstanceOf( NodeResourceApplicationUrlResolver.class );
        assertThat( service.findResolver( applicationKey, "bundle" ) ).isEmpty();
    }

    @Test
    void findResolver_inactiveLocalWithCmsDescriptor()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( ApplicationHelper.toBundleLocation( applicationKey, true ),
                                      newBundle( appName, true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) );

        bundle.start();
        assertThat( service.findResolver( applicationKey, null ) ).isNotEmpty();

        // a local application is never persisted: nothing is served once stopped
        bundle.stop();
        assertThat( service.findResolver( applicationKey, null ) ).isEmpty();
    }

    private static InputStream stream( final String content )
    {
        return new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
    }
}
