package com.enonic.wem.admin.rest.resource.content.page.part;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.part.PartDescriptorJson;
import com.enonic.wem.admin.json.content.page.part.PartTemplateJson;
import com.enonic.wem.admin.json.content.page.part.PartTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.part.GetPartDescriptor;
import com.enonic.wem.api.command.content.page.part.GetPartTemplateByKey;
import com.enonic.wem.api.command.content.page.part.GetPartTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleResourceKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/part/template")
@Produces(MediaType.APPLICATION_JSON)
public class PartTemplateResource
    extends AbstractResource
{
    @GET
    public PartTemplateJson getByKey( @QueryParam("key") final String partTemplateKeyAsString )
        throws IOException
    {
        final PartTemplateKey partTemplateKey = PartTemplateKey.from( partTemplateKeyAsString );
        final GetPartTemplateByKey command = page().template().part().getByKey().key( partTemplateKey );

        final PartTemplate partTemplate = client.execute( command );
        final PartDescriptor descriptor = getDescriptor( partTemplate.getDescriptor() );
        return new PartTemplateJson( partTemplate, new PartDescriptorJson( descriptor ) );
    }

    @Path("list")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public PartTemplateListJson list( @QueryParam("key") String siteTemplateKeyAsString )
    {
        GetPartTemplatesBySiteTemplate command =
            Commands.page().template().part().getBySiteTemplate().siteTemplate( SiteTemplateKey.from( siteTemplateKeyAsString ) );
        PartTemplates partTemplates = this.client.execute( command );

        return new PartTemplateListJson( partTemplates );
    }

    private PartDescriptor getDescriptor( final ModuleResourceKey key )
    {
        final PartDescriptorKey partDescriptorKey = PartDescriptorKey.from( key.getModuleKey(), key.getPath() );
        final GetPartDescriptor getPartDescriptor = page().descriptor().part().getByKey( partDescriptorKey );
        return client.execute( getPartDescriptor );
    }
}

