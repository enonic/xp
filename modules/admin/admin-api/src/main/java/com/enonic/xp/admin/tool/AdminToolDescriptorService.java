package com.enonic.xp.admin.tool;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;


@NullMarked
public interface AdminToolDescriptorService
{
    AdminToolDescriptors getByApplication( ApplicationKey applicationKey );

    @Nullable AdminToolDescriptor getByKey( DescriptorKey descriptorKey );

    AdminToolDescriptors getAll();
}
