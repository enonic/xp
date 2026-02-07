package com.enonic.xp.lib.admin.mapper;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class AdminToolMapper
    implements MapSerializable
{
    private final AdminToolDescriptor descriptor;

    private final boolean systemApp;

    private final String localizedDisplayName;

    private final String localizedDescription;

    private final String icon;

    public AdminToolMapper( final AdminToolDescriptor descriptor, final boolean systemApp, final String localizedDisplayName,
                            final String localizedDescription, final String icon )
    {
        this.descriptor = descriptor;
        this.systemApp = systemApp;
        this.localizedDisplayName = localizedDisplayName;
        this.localizedDescription = localizedDescription;
        this.icon = icon;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", descriptor.getKey() );
        gen.value( "name", localizedDisplayName );
        gen.value( "description", localizedDescription );
        gen.value( "icon", icon );
        gen.value( "systemApp", systemApp );
    }
}
