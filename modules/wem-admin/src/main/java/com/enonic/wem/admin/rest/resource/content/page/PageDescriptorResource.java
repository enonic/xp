package com.enonic.wem.admin.rest.resource.content.page;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageDescriptorJson;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;

@Path("content/page/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PageDescriptorResource
{
    @Inject
    protected PageDescriptorService pageDescriptorService;

    @GET
    public PageDescriptorJson getByKey( @QueryParam("key") final String pageDescriptorKey )
    {
        final PageDescriptorKey key = PageDescriptorKey.from( pageDescriptorKey );
        final PageDescriptor descriptor = pageDescriptorService.getByKey( key );
        final PageDescriptorJson json = new PageDescriptorJson( descriptor );
        return json;
    }
}
