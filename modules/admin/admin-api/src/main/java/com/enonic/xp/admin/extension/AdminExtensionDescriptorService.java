package com.enonic.xp.admin.extension;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.Descriptors;


@NullMarked
public interface AdminExtensionDescriptorService
{
    Descriptors<AdminExtensionDescriptor> getByInterfaces( String... interfaceName );

    Descriptors<AdminExtensionDescriptor> getByApplication( ApplicationKey applicationKey );

    @Nullable AdminExtensionDescriptor getByKey( DescriptorKey descriptorKey );
}
