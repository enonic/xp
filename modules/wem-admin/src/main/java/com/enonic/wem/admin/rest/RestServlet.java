package com.enonic.wem.admin.rest;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import com.enonic.wem.admin.rest.exception.ConflictExceptionMapper;
import com.enonic.wem.admin.rest.exception.DefaultExceptionMapper;
import com.enonic.wem.admin.rest.exception.IllegalArgumentExceptionMapper;
import com.enonic.wem.admin.rest.exception.JsonMappingExceptionMapper;
import com.enonic.wem.admin.rest.exception.NotFoundExceptionMapper;
import com.enonic.wem.admin.rest.multipart.MultipartFormReader;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.admin.rest.provider.RenderedImageProvider;
import com.enonic.wem.admin.rest.resource.auth.AuthResource;
import com.enonic.wem.admin.rest.resource.blob.BlobResource;
import com.enonic.wem.admin.rest.resource.content.ContentAttachmentResource;
import com.enonic.wem.admin.rest.resource.content.ContentIconResource;
import com.enonic.wem.admin.rest.resource.content.ContentImageResource;
import com.enonic.wem.admin.rest.resource.content.ContentResource;
import com.enonic.wem.admin.rest.resource.content.page.PageDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.page.PageResource;
import com.enonic.wem.admin.rest.resource.content.page.PageTemplateResource;
import com.enonic.wem.admin.rest.resource.content.page.image.ImageDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.page.layout.LayoutDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.page.part.PartDescriptorResource;
import com.enonic.wem.admin.rest.resource.content.site.SiteResource;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateIconResource;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateResource;
import com.enonic.wem.admin.rest.resource.module.ModuleResource;
import com.enonic.wem.admin.rest.resource.relationship.RelationshipResource;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconResource;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeResource;
import com.enonic.wem.admin.rest.resource.schema.mixin.MixinResource;
import com.enonic.wem.admin.rest.resource.schema.relationship.RelationshipTypeResource;
import com.enonic.wem.admin.rest.resource.status.StatusResource;
import com.enonic.wem.admin.rest.resource.tools.ToolsResource;
import com.enonic.wem.admin.rest.resource.ui.BackgroundImageResource;
import com.enonic.wem.core.web.servlet.ServletRequestHolder;

@Singleton
public final class RestServlet
    extends HttpServletDispatcher
{
    @Inject
    protected Injector injector;

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( config );
        configure();
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        try
        {
            ServletRequestHolder.setRequest( req );
            super.service( req, resp );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    private void addSingleton( final Class<?> type )
    {
        final Object instance = this.injector.getInstance( type );
        if ( type.getAnnotation( Provider.class ) != null )
        {
            getDispatcher().getProviderFactory().register( instance );
        }
        else
        {
            getDispatcher().getRegistry().addSingletonResource( instance );
        }
    }

    private void configure()
    {
        addSingleton( JsonObjectProvider.class );
        addSingleton( JsonSerializableProvider.class );

        addSingleton( BackgroundImageResource.class );
        addSingleton( ContentImageResource.class );
        addSingleton( ContentIconResource.class );
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

        addSingleton( SchemaIconResource.class );
        addSingleton( MixinResource.class );
        addSingleton( ContentTypeResource.class );

        addSingleton( ModuleResource.class );

        addSingleton( SiteTemplateResource.class );
        addSingleton( SiteTemplateIconResource.class );
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
        addSingleton( RenderedImageProvider.class );
    }
}
