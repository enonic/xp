package com.enonic.wem.admin.rest;

import com.google.inject.Singleton;

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
import com.enonic.wem.admin.rest.resource.content.site.SiteResource;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateResource;
import com.enonic.wem.admin.rest.resource.jcr.GetNodesResource;
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
import com.enonic.wem.admin.rest.resource.util.CountryResource;
import com.enonic.wem.admin.rest.resource.util.LocaleResource;
import com.enonic.wem.admin.rest.resource.util.TimeZoneResource;
import com.enonic.wem.web.jaxrs.JaxRsServlet;

@Singleton
public final class RestServlet
    extends JaxRsServlet
{
    @Override
    protected void configure()
    {
        addClass( JsonObjectProvider.class );
        addClass( JsonSerializableProvider.class );

        addClass( BackgroundImageResource.class );
        addClass( ContentImageResource.class );
        addClass( ContentAttachmentResource.class );
        addClass( GetNodesResource.class );
        addClass( BlobResource.class );
        addClass( AuthResource.class );
        addClass( ToolsResource.class );
        addClass( StatusResource.class );

        addClass( RelationshipResource.class );
        addClass( RelationshipTypeResource.class );

        addClass( ContentResource.class );
        addClass( PageResource.class );
        addClass( SiteResource.class );

        addClass( SchemaResource.class );
        addClass( SchemaImageResource.class );
        addClass( MixinResource.class );
        addClass( ContentTypeResource.class );

        addClass( ModuleResource.class );

        addClass( CountryResource.class );
        addClass( TimeZoneResource.class );
        addClass( LocaleResource.class );

        addClass( SiteTemplateResource.class );
        addClass( PageTemplateResource.class );
        addClass( PageDescriptorResource.class );

        addClass( DefaultExceptionMapper.class );
        addClass( IllegalArgumentExceptionMapper.class );
        addClass( JsonMappingExceptionMapper.class );
        addClass( NotFoundExceptionMapper.class );
    }
}
