package com.enonic.xp.admin.ui.tool.mapper;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class AdminToolMapper
    implements MapSerializable
{
    private final AdminToolDescriptor value;

    public AdminToolMapper( final AdminToolDescriptor value )
    {
        this.value = value;
    }

    private void serialize( final MapGenerator gen, final AdminToolDescriptor value )
    {
        gen.map( "key" );
        gen.value( "application", value.getKey().getApplicationKey().toString() );
        gen.value( "name", value.getKey().getName() );
        gen.end();

        gen.value( "displayName", value.getDisplayName() );
        gen.value( "description", value.getDescription() );
        gen.value( "icon", value.getIcon() );
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

