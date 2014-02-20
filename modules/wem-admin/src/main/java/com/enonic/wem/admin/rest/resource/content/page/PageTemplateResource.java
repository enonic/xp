package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageTemplateJson;
import com.enonic.wem.admin.json.content.page.PageTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateSpec;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.api.command.Commands.page;

@javax.ws.rs.Path("content/page/template")
@Produces(MediaType.APPLICATION_JSON)
public final class PageTemplateResource
    extends AbstractResource
{
    @GET
    public PageTemplateJson getByKey( @QueryParam("siteTemplateKey") final String siteTemplateKeyAsString,
                                      @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = client.execute( page().template().page().getByKey().
            key( pageTemplateKey ).
            siteTemplateKey( siteTemplateKey ) );
        return new PageTemplateJson( pageTemplate );
    }

    @GET
    @javax.ws.rs.Path("list")
    public PageTemplateListJson list( @QueryParam("key") String siteTemplateKeyAsString )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        GetPageTemplatesBySiteTemplate command = Commands.page().template().page().getBySiteTemplate().siteTemplate( siteTemplateKey );
        final PageTemplates pageTemplates = client.execute( command );

        return new PageTemplateListJson( pageTemplates );
    }

    @GET
    @javax.ws.rs.Path("listByCanRender")
    public PageTemplateListJson listByCanRender( @QueryParam("key") String siteTemplateKeyAsString,
                                                 @QueryParam("contentTypeName") String contentTypeName )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        GetPageTemplatesBySiteTemplate command = Commands.page().template().page().getBySiteTemplate().siteTemplate( siteTemplateKey );
        final PageTemplates pageTemplates = client.execute( command );
        final PageTemplateSpec spec = PageTemplateSpec.newPageTemplateSpec().canRender( ContentTypeName.from( contentTypeName ) ).build();

        return new PageTemplateListJson( pageTemplates.filter( spec ) );

    }
}
