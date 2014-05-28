package com.enonic.wem.admin;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

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

public final class AdminModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JsonObjectProvider.class ).in( Singleton.class );
        bind( JsonSerializableProvider.class ).in( Singleton.class );

        bind( BackgroundImageResource.class ).in( Singleton.class );
        bind( ContentImageResource.class ).in( Singleton.class );
        bind( ContentAttachmentResource.class ).in( Singleton.class );
        bind( BlobResource.class ).in( Singleton.class );
        bind( AuthResource.class ).in( Singleton.class );
        bind( ToolsResource.class ).in( Singleton.class );
        bind( StatusResource.class ).in( Singleton.class );

        bind( RelationshipResource.class ).in( Singleton.class );
        bind( RelationshipTypeResource.class ).in( Singleton.class );

        bind( ContentResource.class ).in( Singleton.class );
        bind( PageResource.class ).in( Singleton.class );
        bind( SiteResource.class ).in( Singleton.class );

        bind( SchemaResource.class ).in( Singleton.class );
        bind( SchemaImageResource.class ).in( Singleton.class );
        bind( MixinResource.class ).in( Singleton.class );
        bind( ContentTypeResource.class ).in( Singleton.class );

        bind( ModuleResource.class ).in( Singleton.class );

        bind( SiteTemplateResource.class ).in( Singleton.class );
        bind( PageTemplateResource.class ).in( Singleton.class );
        bind( PageDescriptorResource.class ).in( Singleton.class );
        bind( ImageDescriptorResource.class ).in( Singleton.class );
        bind( PartDescriptorResource.class ).in( Singleton.class );
        bind( LayoutDescriptorResource.class ).in( Singleton.class );

        bind( DefaultExceptionMapper.class ).in( Singleton.class );
        bind( IllegalArgumentExceptionMapper.class ).in( Singleton.class );
        bind( JsonMappingExceptionMapper.class ).in( Singleton.class );
        bind( NotFoundExceptionMapper.class ).in( Singleton.class );
        bind( ConflictExceptionMapper.class ).in( Singleton.class );
    }
}
