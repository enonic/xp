package com.enonic.xp.admin.extension;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.Descriptors;

@PublicApi
public interface AdminExtensionDescriptorService
{
    Descriptors<AdminExtensionDescriptor> getByInterfaces( String... interfaceName );

    Descriptors<AdminExtensionDescriptor> getByApplication( ApplicationKey applicationKey );

    AdminExtensionDescriptor getByKey( DescriptorKey descriptorKey );
}
