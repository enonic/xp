package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageDescriptorJson;
import com.enonic.wem.admin.json.content.page.PageTemplateJson;
import com.enonic.wem.admin.json.content.page.PageTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

@javax.ws.rs.Path("content/page/template")
@Produces(MediaType.APPLICATION_JSON)
public final class PageTemplateResource
    extends AbstractResource
{
    @GET
    public Result getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
    {
        try
        {
            final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
            final PageTemplate pageTemplate = client.execute( page().template().page().getByKey().key( pageTemplateKey ) );
            final PageDescriptor descriptor = PageDescriptorResource.getDescriptor( pageTemplate.getDescriptor(), client );
            return Result.result( new PageTemplateJson( pageTemplate, new PageDescriptorJson( descriptor ) ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @GET
    @javax.ws.rs.Path("list")
    public Result list( @QueryParam("key") String siteTemplateKeyAsString )
    {
        try
        {
            final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
            GetPageTemplatesBySiteTemplate command = Commands.page().template().page().getBySiteTemplate().siteTemplate( siteTemplateKey );
            final PageTemplates pageTemplates = client.execute( command );

            return Result.result( new PageTemplateListJson( pageTemplates ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }
}
