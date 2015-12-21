package com.enonic.xp.admin.ui.adminapp.mapper;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public final class AdminApplicationMapper
    implements MapSerializable
{
    private final AdminApplicationDescriptor value;

    public AdminApplicationMapper( final AdminApplicationDescriptor value )
    {
        this.value = value;
    }

    private void serialize( final MapGenerator gen, final AdminApplicationDescriptor value )
    {
        gen.map( "key" );
        gen.value( "application", value.getKey().getApplicationKey().toString() );
        gen.value( "name", value.getKey().getName() );
        gen.end();

        gen.value( "displayName", value.getDisplayName() );
        gen.value( "icon", value.getIcon() );

        serializeAllowedPrincipals( gen, value.getAllowedPrincipals() );
    }

    private static void serializeAllowedPrincipals( final MapGenerator gen, final PrincipalKeys value )
    {
        gen.array( "allow" );
        for ( final PrincipalKey principalKey : value )
        {
            gen.value( principalKey.toString() );
        }
        gen.end();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

