package com.enonic.wem.admin.rest.resource.content.site.template;

import java.awt.image.BufferedImage;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;

@Path("sitetemplate/image")
@Produces("image/*")
public final class SiteTemplateImageResource
{
    private static final String DEFAULT_MIME_TYPE = "image/png";

    private static final SiteTemplateImageHelper helper = new SiteTemplateImageHelper();

    private SiteTemplateService siteTemplateService;

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    @GET
    @Path("{siteTemplateKey}")
    public Response getSiteTemplateIcon( @PathParam("siteTemplateKey") final String siteTemplate,
                                   @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( 3600 );

        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplate );

        final Icon icon = findIcon( siteTemplateKey );

        if ( icon != null )
        {
            return Response.ok( helper.resizeImage( icon.asInputStream(), size ), icon.getMimeType() ).cacheControl( cacheControl ).build();
        }
        else
        {
            final BufferedImage defaultImage = helper.getDefaultSiteTemplateImage( size );
            return Response.ok( defaultImage, DEFAULT_MIME_TYPE ).cacheControl( cacheControl ).build();
        }
    }

    private Icon findIcon( final SiteTemplateKey siteTemplateKey )
    {
        final SiteTemplate siteTemplate = siteTemplateService.getSiteTemplate( siteTemplateKey );
        return siteTemplate == null ? null : siteTemplate.getIcon();
    }

}
