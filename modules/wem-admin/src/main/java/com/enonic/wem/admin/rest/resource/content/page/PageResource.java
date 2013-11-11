package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.page.json.CreatePageJson;
import com.enonic.wem.admin.rest.resource.content.page.json.CreatePageParamsJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.CreatePage;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;

@Path("content/page")
@Produces(MediaType.APPLICATION_JSON)
public class PageResource
    extends AbstractResource
{
    private RootDataSetJsonSerializer rootDataSetSerializer = new RootDataSetJsonSerializer();

    @POST
    @Path("create")
    public CreatePageJson create( final CreatePageParamsJson params )
    {
        final RootDataSet configAsRootDataSet = rootDataSetSerializer.parse( params.getConfig() );
        final RootDataSet pageAsRootDataSet = rootDataSetSerializer.parse( params.getPage() );
        final RootDataSet liveEditAsRootDataSet = rootDataSetSerializer.parse( params.getLiveEdit() );

        final CreatePage createPage = Commands.page().create().
            content( params.getContentId() ).
            pageTemplate( params.getPageTemplateName() ).
            config( configAsRootDataSet );

        client.execute( createPage );

        return null;
    }

}
