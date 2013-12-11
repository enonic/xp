package com.enonic.wem.admin.rest.resource.content.page.layout;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.layout.LayoutDescriptorJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutTemplateJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.layout.GetLayoutTemplateByKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/layout/template")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutTemplateResource
    extends AbstractResource
{
    @GET
    public Result getByKey( @QueryParam("key") final String layoutTemplateKeyAsString )
    {
        try
        {
            final LayoutTemplateKey layoutTemplateKey = LayoutTemplateKey.from( layoutTemplateKeyAsString );
            final GetLayoutTemplateByKey command = page().template().layout().getByKey().key( layoutTemplateKey );

            final LayoutTemplate layoutTemplate = client.execute( command );
            final LayoutDescriptor descriptor = LayoutDescriptorResource.getDescriptor( layoutTemplate.getDescriptor(), client );
            return Result.result( new LayoutTemplateJson( layoutTemplate, new LayoutDescriptorJson( descriptor ) ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @GET
    @Path( "list" )
    public Result listLayoutTemplates( @QueryParam( "key" ) final String siteTemplateKeyAsString )
    {
        try
        {
            SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );

            LayoutTemplates layoutTemplates = client.execute(
                Commands.page().template().layout().getBySiteTemplate().siteTemplate( siteTemplateKey ) );
            return Result.result( new LayoutTemplateListJson( layoutTemplates ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

}
