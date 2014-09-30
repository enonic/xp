package com.enonic.wem.admin.rest.resource.content.site.template;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.admin.rest.resource.schema.SchemaImageHelper;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;

@Path("sitetemplate/icon")
@Produces("image/*")
public final class SiteTemplateIconResource
{
    private static final SchemaImageHelper helper = new SchemaImageHelper();

    private SiteTemplateService siteTemplateService;

    private ContentTypeIconResolver contentTypeIconResolver;

    @GET
    @Path("{siteTemplateKey}")
    public Response getSiteTemplateIcon( @PathParam("siteTemplateKey") final String siteTemplate,
                                         @QueryParam("size") @DefaultValue("128") final int size, @QueryParam("hash") final String hash )
        throws Exception
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplate );
        final Icon icon = findSiteTemplateIcon( siteTemplateKey );
        if ( icon != null )
        {
            Response.ResponseBuilder responseBuilder = Response.ok( helper.resizeImage( icon.asInputStream(), size ), icon.getMimeType() );
            if ( StringUtils.isNotEmpty( hash ) )
            {
                final CacheControl cacheControl = new CacheControl();
                cacheControl.setMaxAge( Integer.MAX_VALUE );
                responseBuilder.cacheControl( cacheControl );
            }
            return responseBuilder.build();
        }
        else
        {
            final Icon siteIcon = contentTypeIconResolver.resolveIcon( ContentTypeName.site() );
            return Response.ok( helper.resizeImage( siteIcon.asInputStream(), size ), siteIcon.getMimeType() ).build();
        }
    }

    private Icon findSiteTemplateIcon( final SiteTemplateKey siteTemplateKey )
    {
        final SiteTemplate siteTemplate = siteTemplateService.getSiteTemplate( siteTemplateKey );
        return siteTemplate == null ? null : siteTemplate.getIcon();
    }

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
    }

}
