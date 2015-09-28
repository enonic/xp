package com.enonic.xp.testing.script;

import java.net.URL;
import java.util.Map;
import java.util.function.Supplier;

import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.bean.BeanContext;

public abstract class ScriptBeanTestSupport
{
    private final static ApplicationKey DEFAULT_APPLICATION_KEY = ApplicationKey.from( "myapplication" );

    private final Map<Class, Object> services;

    protected final PortalRequest portalRequest;

    protected final ResourceService resourceService;

    private final Application application;

    public ScriptBeanTestSupport()
    {
        this.services = Maps.newHashMap();

        this.application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( Mockito.mock( Bundle.class ) );
        Mockito.when( application.getKey() ).thenReturn( DEFAULT_APPLICATION_KEY );
        Mockito.when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( DEFAULT_APPLICATION_KEY ) ).thenReturn( application );
        Mockito.when( applicationService.getClassLoader( Mockito.any() ) ).thenReturn( getClass().getClassLoader() );

        resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl = ScriptBeanTestSupport.class.getResource( resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        addService( ResourceService.class, resourceService );
        this.portalRequest = new PortalRequest();
    }

    protected final void setupRequest()
    {
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        this.portalRequest.setContent( content );

        PortalRequestAccessor.set( this.portalRequest );
    }

    protected final BeanContext newBeanContext( final ResourceKey resourceKey )
    {
        return new BeanContext()
        {
            @Override
            public Application getApplication()
            {
                return application;
            }

            @Override
            public ResourceKey getResourceKey()
            {
                return resourceKey;
            }

            @Override
            public <T> Supplier<T> getAttribute( final Class<T> type )
            {
                return () -> null;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> Supplier<T> getService( final Class<T> type )
            {
                return () -> (T) services.get( type );
            }
        };
    }

    protected final <T> void addService( final Class<T> type, final T instance )
    {
        this.services.put( type, instance );
    }
}
