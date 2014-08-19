package com.enonic.wem.admin.rest.resource.content;

import java.util.HashMap;

import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateIconUrlResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.GetAttachmentParameters;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.ImageContentType;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class ContentIconUrlResolver
{
    static final Context STAGE_CONTEXT = new Context( ContentConstants.WORKSPACE_STAGE );

    private ContentTypeService contentTypeService;

    private AttachmentService attachmentService;

    private SiteTemplateService siteTemplateService;

    private SchemaIconResolver schemaIconResolver;

    private SchemaIconUrlResolver schemaIconUrlResolver;

    private HashMap<SiteTemplateKey, SiteTemplate> siteTemplatesByKey = new HashMap<>();

    public ContentIconUrlResolver( final SiteTemplateService siteTemplateService, final ContentTypeService contentTypeService,
                                   final AttachmentService attachmentService )
    {
        this.siteTemplateService = siteTemplateService;
        this.contentTypeService = contentTypeService;
        this.schemaIconResolver = new SchemaIconResolver( contentTypeService );
        this.schemaIconUrlResolver = new SchemaIconUrlResolver( this.schemaIconResolver );
        this.attachmentService = attachmentService;
    }

    public String resolve( final Content content )
    {
        if ( content.hasThumbnail() )
        {
            return ServletRequestUrlHelper.createUri(
                "/admin/rest/content/icon/" + content.getId() + "?ts=" + content.getModifiedTime().toEpochMilli() );
        }
        else if ( content.getType().isImageMedia() )
        {
            final String attachmentName = ImageContentType.getImageAttachmentName( content );
            final Attachment attachment = attachmentService.get( GetAttachmentParameters.create().
                contentId( content.getId() ).
                attachmentName( attachmentName ).
                context( STAGE_CONTEXT ).
                build() );
            if ( attachment != null )
            {
                return ServletRequestUrlHelper.createUri(
                    "/admin/rest/content/icon/" + content.getId() + "?ts=" + content.getModifiedTime().toEpochMilli() );
            }
            else
            {
                return new SchemaIconUrlResolver( this.schemaIconResolver ).resolve(
                    this.contentTypeService.getByName( GetContentTypeParams.from( ContentTypeName.imageMedia() ) ) );
            }
        }
        else if ( content.isSite() )
        {
            final SiteTemplate siteTemplate = getSiteTemplate( content.getSite().getTemplate() );
            if ( siteTemplate != null && siteTemplate.getIcon() != null )
            {
                return ServletRequestUrlHelper.createUri(
                    new SiteTemplateIconUrlResolver( schemaIconUrlResolver ).resolve( siteTemplate ) );
            }
        }
        return new SchemaIconUrlResolver( schemaIconResolver ).resolve( content.getType() );
    }

    private SiteTemplate getSiteTemplate( final SiteTemplateKey key )
    {
        SiteTemplate siteTemplate = this.siteTemplatesByKey.get( key );
        if ( siteTemplate == null )
        {
            siteTemplate = siteTemplateService.getSiteTemplate( key );
            if ( siteTemplate != null )
            {
                siteTemplatesByKey.put( siteTemplate.getKey(), siteTemplate );
            }
        }
        return siteTemplate;
    }
}
