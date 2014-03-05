package com.enonic.wem.core.content.page.text;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.text.GetTextDescriptor;
import com.enonic.wem.api.content.page.text.GetTextDescriptorsByModules;
import com.enonic.wem.api.content.page.text.TextDescriptor;
import com.enonic.wem.api.content.page.text.TextDescriptorKey;
import com.enonic.wem.api.content.page.text.TextDescriptorService;
import com.enonic.wem.api.content.page.text.TextDescriptors;
import com.enonic.wem.api.module.ModuleKeys;

public class TextDescriptorServiceImpl
    implements TextDescriptorService
{
    @Inject
    private Client client;

    public TextDescriptor getByKey( final TextDescriptorKey key )
    {
        final GetTextDescriptor command = new GetTextDescriptor( key );
        return client.execute( command );
    }

    public TextDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        final GetTextDescriptorsByModules command = new GetTextDescriptorsByModules( moduleKeys );
        return client.execute( command );
    }
}
