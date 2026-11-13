package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

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

    // the bundle and the resolver come of one lookup: the active bundle with the resolver of the running application, none with
    // the resolver of a stopped one
    @Test
    void findResourceSource()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        // installed but not started: a global application serves its persisted descriptor (and schema, if any)
        ResourceSource source = service.findResourceSource( applicationKey, null );
        assertThat( source.bundle() ).isNull();
        assertThat( source.resolver() ).isInstanceOf( NodeResourceApplicationUrlResolver.class );

        bundle.start();
        source = service.findResourceSource( applicationKey, null );
        assertThat( source.bundle() ).isEqualTo( bundle );
        assertThat( source.resolver() ).isInstanceOf( MultiApplicationUrlResolver.class );

        source = service.findResourceSource( applicationKey, "bundle" );
        assertThat( source.bundle() ).isEqualTo( bundle );
        assertThat( source.resolver() ).isInstanceOf( BundleApplicationUrlResolver.class );

        bundle.stop();
        source = service.findResourceSource( applicationKey, null );
        assertThat( source.bundle() ).isNull();
        assertThat( source.resolver() ).isInstanceOf( NodeResourceApplicationUrlResolver.class );
    }

    @Test
    void findResourceSource_local()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        // a local application is never persisted: nothing is served until it is started, then the bundle only
        final Bundle bundle = deploy( "local:" + appName, newBundle( appName, true ) );

        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isNull();

        bundle.start();
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isInstanceOf( BundleApplicationUrlResolver.class );

        bundle.stop();
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isNull();
    }

    @Test
    void findResourceSource_inactiveWithPersistedSchema()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) );

        // installed but not started: the persisted schema is served
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isInstanceOf( NodeResourceApplicationUrlResolver.class );
        assertThat( service.findResourceSource( applicationKey, "bundle" ).resolver() ).isNull();

        bundle.start();
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isNotNull().isNotInstanceOf( NodeResourceApplicationUrlResolver.class );

        // stopped: the persisted schema is still served, bundle resources are not
        bundle.stop();
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isInstanceOf( NodeResourceApplicationUrlResolver.class );
        assertThat( service.findResourceSource( applicationKey, "bundle" ).resolver() ).isNull();
    }

    @Test
    void findResourceSource_inactiveLocalWithCmsDescriptor()
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
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isNotNull();

        // a local application is never persisted: nothing is served once stopped
        bundle.stop();
        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isNull();
    }

    @Test
    void findResourceSource_persistedOnlyApplication()
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        // no bundle at all: an application created through the descriptor service serves its persisted descriptor and schema
        final ApplicationKey applicationKey = ApplicationKey.from( "nodeonly" );
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.DESCRIPTOR, "kind: \"Application\"\n" );
        when( nodeService.getByPath( new NodePath( "/nodeonly" ) ) ).thenReturn(
            Node.create().id( new NodeId() ).name( "nodeonly" ).parentPath( NodePath.ROOT ).data( data ).build() );

        assertThat( service.findResourceSource( applicationKey, null ).resolver() ).isInstanceOf( NodeResourceApplicationUrlResolver.class );
        assertThat( service.findResourceSource( applicationKey, "bundle" ).resolver() ).isNull();

        // nothing persisted, nothing installed: nothing is served
        assertThat( service.findResourceSource( ApplicationKey.from( "unknown" ), null ).resolver() ).isNull();
    }

    private static InputStream stream( final String content )
    {
        return new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
    }
}
