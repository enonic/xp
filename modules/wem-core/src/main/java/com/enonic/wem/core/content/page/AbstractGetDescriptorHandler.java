package com.enonic.wem.core.content.page;

import java.io.IOException;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.command.CommandHandler;

public abstract class AbstractGetDescriptorHandler<C extends Command>
    extends CommandHandler<C>
{
    protected String readDescriptorXml( final DescriptorKey key )
        throws IOException
    {
        final ModuleResourceKey resourceKey = new ModuleResourceKey( key.getModuleKey(), key.getPath() );
        final GetModuleResource getModuleResource = Commands.module().getResource().resourceKey( resourceKey );
        final Resource descriptorResource = context.getClient().execute( getModuleResource );
        final String descriptorXml = descriptorResource.readAsString();

        return descriptorXml;
    }
}
