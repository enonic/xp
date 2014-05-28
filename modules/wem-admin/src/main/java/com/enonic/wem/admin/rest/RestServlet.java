package com.enonic.wem.admin.rest;

import com.google.inject.Singleton;

import com.enonic.wem.admin.rest.exception.ConflictExceptionMapper;
import com.enonic.wem.admin.rest.exception.DefaultExceptionMapper;
import com.enonic.wem.admin.rest.exception.IllegalArgumentExceptionMapper;
import com.enonic.wem.admin.rest.exception.JsonMappingExceptionMapper;
import com.enonic.wem.admin.rest.exception.NotFoundExceptionMapper;
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
import com.enonic.wem.core.web.jaxrs.JaxRsServlet;

@Singleton
public final class RestServlet
    extends JaxRsServlet
{
    @Override
    protected void configure()
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
    }
}
