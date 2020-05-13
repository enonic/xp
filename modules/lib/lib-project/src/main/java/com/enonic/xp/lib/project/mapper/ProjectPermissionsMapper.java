package com.enonic.xp.lib.project.mapper;

import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKeys;

public final class ProjectPermissionsMapper
    implements MapSerializable
{
    private final ProjectPermissions permissions;

    public ProjectPermissionsMapper( final ProjectPermissions permissions )
    {
        this.permissions = permissions;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        doSerialize( gen, this.permissions );
    }

    private void doSerialize( final MapGenerator gen, final ProjectPermissions value )
    {
        if ( value != null )
        {
            gen.map( "permissions" );
            mapPrincipals( gen, value.getOwner(), "owner" );
            mapPrincipals( gen, value.getEditor(), "editor" );
            mapPrincipals( gen, value.getAuthor(), "author" );
            mapPrincipals( gen, value.getContributor(), "contributor" );
            mapPrincipals( gen, value.getViewer(), "viewer" );
            gen.end();
        }
    }

    private void mapPrincipals( final MapGenerator gen, final PrincipalKeys principalKeys, final String name )
    {
        if ( !principalKeys.isEmpty() )
        {
            gen.array( name );
            principalKeys.forEach( gen::value );
            gen.end();
        }
    }
}
