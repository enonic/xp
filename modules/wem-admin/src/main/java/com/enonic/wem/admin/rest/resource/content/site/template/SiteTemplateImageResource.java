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

import com.enonic.wem.admin.rest.resource.schema.SchemaImageHelper;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageResource;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

@Path("sitetemplate/image")
@Produces("image/*")
public final class SiteTemplateImageResource
{
    private static final SchemaImageHelper helper = new SchemaImageHelper();

    private SiteTemplateService siteTemplateService;

    private SchemaImageResource schemaImageResource;

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    @Inject
    public void setSchemaImageResource( final SchemaImageResource schemaImageResource )
    {
        this.schemaImageResource = schemaImageResource;
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
            final Icon siteIcon = schemaImageResource.resolveContentTypeImage( SchemaKey.from( ContentTypeName.site() ) );
            return Response.ok( helper.resizeImage( siteIcon.asInputStream(), size ), siteIcon.getMimeType() ).
                cacheControl( cacheControl ).
                build();
        }
    }

    private Icon findIcon( final SiteTemplateKey siteTemplateKey )
    {
        final SiteTemplate siteTemplate = siteTemplateService.getSiteTemplate( siteTemplateKey );
        return siteTemplate == null ? null : siteTemplate.getIcon();
    }

}
