package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.admin.json.content.page.PageDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.xml.XmlSerializers;

@Path("content/page/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PageDescriptorResource
    extends AbstractResource
{
    @GET
    public Result getByKey( @QueryParam("key") final String pageDescriptorModuleResourceKey )
    {
        try
        {
            final ModuleResourceKey key = ModuleResourceKey.from( pageDescriptorModuleResourceKey );
            final GetModuleResource command = Commands.module().getResource().resourceKey( key );
            final Resource descriptorResource = client.execute( command );
            final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
            XmlSerializers.pageDescriptor().parse( descriptorResource.readAsString() ).to( builder );

            final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
            builder.name( descriptorName );

            final PageDescriptorJson json = new PageDescriptorJson( builder.build() );
            return Result.result( json );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }
}
