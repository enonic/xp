package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.SchemaResult;

public class DescriptorConverter
{
    private DescriptorConverter()
    {
    }

    public static DescriptorMapper convert( final SchemaResult<? extends ComponentDescriptor> descriptor )
    {
        final Object dynamicSchema = descriptor.getSchema();
        if ( dynamicSchema instanceof PartDescriptor )
        {
            return new PartDescriptorMapper( (SchemaResult<PartDescriptor>) descriptor );
        }
        if ( dynamicSchema instanceof LayoutDescriptor )
        {
            return new LayoutDescriptorMapper( (SchemaResult<LayoutDescriptor>) descriptor );
        }
        if ( dynamicSchema instanceof PageDescriptor )
        {
            return new PageDescriptorMapper( (SchemaResult<PageDescriptor>) descriptor );
        }

        throw new IllegalArgumentException( "invalid component type: " + descriptor.getClass() );
    }
}
