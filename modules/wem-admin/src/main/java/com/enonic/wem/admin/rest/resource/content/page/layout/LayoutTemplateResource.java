package com.enonic.wem.admin.rest.resource.content.page.layout;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.layout.LayoutDescriptorJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutTemplateJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptor;
import com.enonic.wem.api.command.content.page.layout.GetLayoutTemplateByKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
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
    public LayoutTemplateJson getByKey( @QueryParam("key") final String layoutTemplateKeyAsString )
        throws IOException
    {
        final LayoutTemplateKey layoutTemplateKey = LayoutTemplateKey.from( layoutTemplateKeyAsString );
        final GetLayoutTemplateByKey command = page().template().layout().getByKey().key( layoutTemplateKey );

        final LayoutTemplate layoutTemplate = client.execute( command );
        final LayoutDescriptor descriptor = getDescriptor( layoutTemplate.getDescriptor() );
        return new LayoutTemplateJson( layoutTemplate, new LayoutDescriptorJson( descriptor ) );
    }

    @GET
    @Path("list")
    public LayoutTemplateListJson listLayoutTemplates( @QueryParam("key") final String siteTemplateKeyAsString )
    {
        SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );

        LayoutTemplates layoutTemplates =
            client.execute( Commands.page().template().layout().getBySiteTemplate().siteTemplate( siteTemplateKey ) );

        return new LayoutTemplateListJson( layoutTemplates );
    }

    private LayoutDescriptor getDescriptor( final LayoutDescriptorKey key )
    {
        final GetLayoutDescriptor getLayoutDescriptor = page().descriptor().layout().getByKey( key );
        return client.execute( getLayoutDescriptor );
    }
}
