package com.enonic.wem.admin.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

import com.enonic.wem.admin.rest.exception.ConflictExceptionMapper;
import com.enonic.wem.admin.rest.exception.DefaultExceptionMapper;
import com.enonic.wem.admin.rest.exception.IllegalArgumentExceptionMapper;
import com.enonic.wem.admin.rest.exception.JsonMappingExceptionMapper;
import com.enonic.wem.admin.rest.exception.NotFoundExceptionMapper;
import com.enonic.wem.admin.rest.multipart.MultipartFormReader;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.admin.rest.resource.auth.AuthResource;
import com.enonic.wem.admin.rest.resource.blob.BlobResource;
import com.enonic.wem.admin.rest.resource.content.ContentAttachmentResource;
import com.enonic.wem.admin.rest.resource.content.ContentImageResource;
import com.enonic.wem.admin.rest.resource.content.ContentResource;
import com.enonic.wem.admin.rest.resource.content.page.PageDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.page.PageResource;
import com.enonic.wem.admin.rest.resource.content.page.PageTemplateResource;
import com.enonic.wem.admin.rest.resource.content.page.image.ImageDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.page.layout.LayoutDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.page.part.PartDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.site.SiteResource;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateImageResource;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateResource;
import com.enonic.wem.admin.rest.resource.module.ModuleResource;
import com.enonic.wem.admin.rest.resource.relationship.RelationshipResource;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageResource;
import com.enonic.wem.admin.rest.resource.schema.SchemaResource;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeResource;
import com.enonic.wem.admin.rest.resource.schema.mixin.MixinResource;
import com.enonic.wem.admin.rest.resource.schema.relationship.RelationshipTypeResource;
import com.enonic.wem.admin.rest.resource.status.StatusResource;
import com.enonic.wem.admin.rest.resource.tools.ToolsResource;
import com.enonic.wem.admin.rest.resource.ui.BackgroundImageResource;
import com.enonic.wem.core.web.servlet.ServletRequestHolder;

@Singleton
public final class RestServlet
    extends HttpServlet
{
    private final class Container
        extends ServletContainer
    {
        @Override
        protected ResourceConfig getDefaultResourceConfig( final Map<String, Object> props, final WebConfig wc )
            throws ServletException
        {
            RestServlet.this.config.getProperties().putAll( props );
            return RestServlet.this.config;
        }

        @Override
        protected void initiate( final ResourceConfig rc, final WebApplication wa )
        {
            RestServlet.this.configure();
            wa.initiate( rc );
        }
    }

    private final DefaultResourceConfig config;

    @Inject
    protected Injector injector;

    private final Container container;

    public RestServlet()
    {
        this.config = new DefaultResourceConfig();
        this.container = new Container();
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        this.container.init( config );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        try
        {
            ServletRequestHolder.setRequest( req );
            this.container.service( req, resp );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    @Override
    public void destroy()
    {
        this.container.destroy();
    }

    private void addSingleton( final Class<?> type )
    {
        this.config.getSingletons().add( this.injector.getInstance( type ) );
    }

    private void configure()
    {
        addSingleton( JsonObjectProvider.class );
        addSingleton( JsonSerializableProvider.class );

        addSingleton( BackgroundImageResource.class );
        addSingleton( ContentImageResource.class );
        addSingleton( ContentAttachmentResource.class );
        addSingleton( BlobResource.class );
        addSingleton( AuthResource.class );
        addSingleton( ToolsResource.class );
        addSingleton( StatusResource.class );

        addSingleton( RelationshipResource.class );
        addSingleton( RelationshipTypeResource.class );

        addSingleton( ContentResource.class );
        addSingleton( PageResource.class );
        addSingleton( SiteResource.class );

        addSingleton( SchemaResource.class );
        addSingleton( SchemaImageResource.class );
        addSingleton( MixinResource.class );
        addSingleton( ContentTypeResource.class );

        addSingleton( ModuleResource.class );

        addSingleton( SiteTemplateResource.class );
        addSingleton( SiteTemplateImageResource.class );
        addSingleton( PageTemplateResource.class );
        addSingleton( PageDescriptorResource.class );
        addSingleton( ImageDescriptorResource.class );
        addSingleton( PartDescriptorResource.class );
        addSingleton( LayoutDescriptorResource.class );

        addSingleton( DefaultExceptionMapper.class );
        addSingleton( IllegalArgumentExceptionMapper.class );
        addSingleton( JsonMappingExceptionMapper.class );
        addSingleton( NotFoundExceptionMapper.class );
        addSingleton( ConflictExceptionMapper.class );

        addSingleton( MultipartFormReader.class );
    }
}
