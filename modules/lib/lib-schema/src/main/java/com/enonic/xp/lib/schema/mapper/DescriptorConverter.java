package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;

public class DescriptorConverter
{
    public static DescriptorMapper convert( final DynamicSchemaResult<? extends ComponentDescriptor> descriptor )
    {
        final Object dynamicSchema = descriptor.getSchema();
        if ( dynamicSchema instanceof PartDescriptor )
        {
            return new PartDescriptorMapper( (DynamicSchemaResult<PartDescriptor>) descriptor );
        }
        if ( dynamicSchema instanceof LayoutDescriptor )
        {
            return new LayoutDescriptorMapper( (DynamicSchemaResult<LayoutDescriptor>) descriptor );
        }
        if ( dynamicSchema instanceof PageDescriptor )
        {
            return new PageDescriptorMapper( (DynamicSchemaResult<PageDescriptor>) descriptor );
        }

        throw new IllegalArgumentException( "invalid component type: " + descriptor.getClass() );
    }
}
