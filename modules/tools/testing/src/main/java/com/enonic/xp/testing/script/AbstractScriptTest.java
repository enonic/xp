package com.enonic.xp.testing.script;

import java.net.URL;
import java.util.Map;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;

public abstract class AbstractScriptTest
{
    protected PortalRequest portalRequest;

    protected Map<Class, Object> services;

    protected ApplicationKey applicationKey;

    protected ResourceService resourceService;

    public AbstractScriptTest()
    {
        setApplicationKey( "myapplication" );
    }

    @Before
    public final void setup()
    {
        this.services = Maps.newHashMap();
        setupRequest();
        initialize();
    }

    private void setupRequest()
    {
        this.portalRequest = new PortalRequest();

        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        this.portalRequest.setContent( content );

        PortalRequestAccessor.set( this.portalRequest );
    }

    protected void initialize()
    {
        this.resourceService = createResourceService();
        addService( ResourceService.class, this.resourceService );

        final ViewFunctionService viewFunctionService = new ViewFunctionsMockFactory().newService();
        addService( ViewFunctionService.class, viewFunctionService );
    }

    protected final <T> void addService( final Class<T> type, final T instance )
    {
        this.services.put( type, instance );
    }

    protected final void setApplicationKey( final String name )
    {
        this.applicationKey = ApplicationKey.from( name );
    }

    private ResourceService createResourceService()
    {
        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( this::loadResource );

        addService( ResourceService.class, resourceService );
        return resourceService;
    }

    private Resource loadResource( final InvocationOnMock invocation )
    {
        return loadResource( (ResourceKey) invocation.getArguments()[0] );
    }

    protected final Resource loadResource( final String path )
    {
        return loadResource( ResourceKey.from( this.applicationKey, path ) );
    }

    private Resource loadResource( final ResourceKey key )
    {
        final URL url = findResource( key.getPath() );
        return new UrlResource( key, url );
    }

    private URL findResource( final String path )
    {
        return getClass().getResource( path );
    }
}
