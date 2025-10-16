package com.enonic.xp.core.impl.schema.fragment;

import java.time.Instant;

import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;

final class CmsFormFragmentLoader
    extends SchemaLoader<FormFragmentName, FormFragmentDescriptor>
{
    CmsFormFragmentLoader( final ResourceService resourceService )
    {
        super( resourceService, "/cms/form-fragments" );
    }

    @Override
    protected FormFragmentDescriptor load( final FormFragmentName name, final Resource resource )
    {
        final FormFragmentDescriptor.Builder builder = YmlFormFragmentParser.parse( resource.readString(), name.getApplicationKey() );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    @Override
    protected FormFragmentName newName( final DescriptorKey descriptorKey )
    {
        return FormFragmentName.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
    }
}
