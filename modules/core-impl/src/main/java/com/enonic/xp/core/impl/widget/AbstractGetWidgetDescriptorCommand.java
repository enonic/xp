package com.enonic.xp.core.impl.widget;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.widget.WidgetDescriptor;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlWidgetDescriptorParser;

abstract class AbstractGetWidgetDescriptorCommand<T extends AbstractGetWidgetDescriptorCommand>
{

    protected WidgetDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey =
            ResourceKey.from( key.getApplicationKey(), "/widgets/" + key.getName() + "/" + key.getName() + ".xml" );
        final Resource resource = Resource.from( resourceKey );

        final WidgetDescriptor.Builder builder = WidgetDescriptor.create();

        if ( resource.exists() )
        {
            final String descriptorXml = resource.readString();
            try
            {
                parseXml( resourceKey.getApplicationKey(), builder, descriptorXml );
            }
            catch ( final Exception e )
            {
                throw new XmlException( e, "Could not load widget descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
            }
        }
        else
        {
            builder.displayName( key.getName() );
        }

        builder.key( key );

        return builder.build();
    }

    private void parseXml( final ApplicationKey applicationKey, final WidgetDescriptor.Builder builder, final String xml )
    {
        final XmlWidgetDescriptorParser parser = new XmlWidgetDescriptorParser();
        parser.builder( builder );
        parser.currentModule( applicationKey );
        parser.source( xml );
        parser.parse();
    }
}
