package com.enonic.wem.admin.rest.resource.content.page.text;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.text.TextDescriptorJson;
import com.enonic.wem.admin.json.content.page.text.TextDescriptorsJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.content.page.text.TextDescriptor;
import com.enonic.wem.api.content.page.text.TextDescriptorKey;
import com.enonic.wem.api.content.page.text.TextDescriptorService;
import com.enonic.wem.api.content.page.text.TextDescriptors;

@Path("content/page/text/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class TextDescriptorResource
    extends AbstractResource
{
    @Inject
    protected TextDescriptorService textDescriptorService;

    @GET
    public TextDescriptorJson getByKey( @QueryParam("key") final String textDescriptorKey )
    {
        final TextDescriptorKey key = TextDescriptorKey.from( textDescriptorKey );
        final TextDescriptor descriptor = textDescriptorService.getByKey( key );
        return new TextDescriptorJson( descriptor );
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public TextDescriptorsJson getByModules( final GetByModulesParams params )
    {
        final TextDescriptors descriptors = textDescriptorService.getByModules( params.getModuleKeys() );
        return new TextDescriptorsJson( descriptors );
    }
}
