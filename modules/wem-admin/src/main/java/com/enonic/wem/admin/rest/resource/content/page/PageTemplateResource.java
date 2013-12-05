package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageTemplateJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/template")
@Produces(MediaType.APPLICATION_JSON)
public class PageTemplateResource
    extends AbstractResource
{
    @GET
    public Result getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
    {
        try
        {
            final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
            final PageTemplate pageTemplate = client.execute( page().template().page().getByKey().key( pageTemplateKey ) );
            return Result.result( new PageTemplateJson( pageTemplate ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }
}
