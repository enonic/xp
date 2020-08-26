package com.enonic.xp.lib.project.mapper;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ProjectReadAccessMapper
    implements MapSerializable
{
    private final Boolean isPublic;

    public ProjectReadAccessMapper( final Boolean isPublic )
    {
        this.isPublic = isPublic;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        doSerialize( gen, this.isPublic );
    }

    private void doSerialize( final MapGenerator gen, final Boolean value )
    {
        if ( value != null )
        {
            gen.map( "readAccess" );
            gen.value( "public", isPublic );
            gen.end();
        }
    }
}
