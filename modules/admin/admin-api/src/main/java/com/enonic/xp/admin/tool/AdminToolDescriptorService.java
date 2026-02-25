package com.enonic.xp.admin.tool;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public interface AdminToolDescriptorService
{
    AdminToolDescriptors getByApplication( ApplicationKey applicationKey );

    AdminToolDescriptor getByKey( DescriptorKey descriptorKey );

    AdminToolDescriptors getAll();
}
