package com.enonic.wem.admin.rest.resource.content;

import java.util.HashMap;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class ContentIconUrlResolver
{
    private SiteTemplateService siteTemplateService;

    private ContentTypeService contentTypeService;

    private HashMap<ContentTypeName, ContentType> contentTypesByName = new HashMap<>();

    private HashMap<SiteTemplateKey, SiteTemplate> siteTemplatesByKey = new HashMap<>();

    public ContentIconUrlResolver( final SiteTemplateService siteTemplateService, final ContentTypeService contentTypeService )
    {
        this.siteTemplateService = siteTemplateService;
        this.contentTypeService = contentTypeService;
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
            return ServletRequestUrlHelper.createUri(
                "/admin/rest/content/icon/" + content.getId() + "?ts=" + content.getModifiedTime().toEpochMilli() );
        }
        else if ( content.isSite() )
        {
            final SiteTemplate siteTemplate = getSiteTemplate( content.getSite().getTemplate() );
            if ( siteTemplate != null && siteTemplate.getIcon() != null )
            {
                return ServletRequestUrlHelper.createUri( "/admin/rest/sitetemplate/icon/" + siteTemplate.getKey() + "?ts=" +
                                                              siteTemplate.getIcon().getModifiedTime().toEpochMilli() );
            }
        }

        final ContentType contentType = resolveSuperContentTypeWithIcon( content.getType() );
        Preconditions.checkState( contentType != null,
                                  "Expected system to provide a super ContentType with an Icon for: " + content.getType() );
        return SchemaIconUrlResolver.resolve( contentType );
    }

    private ContentType resolveSuperContentTypeWithIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null && contentType.getSuperType() != null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        ContentType contentType = contentTypesByName.get( contentTypeName );
        if ( contentType == null )
        {
            contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
        }
        return contentType;
    }

    private SiteTemplate getSiteTemplate( final SiteTemplateKey key )
    {
        SiteTemplate siteTemplate = this.siteTemplatesByKey.get( key );
        if ( siteTemplate == null )
        {
            siteTemplate = siteTemplateService.getSiteTemplate( key );
        }
        return siteTemplate;
    }

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
