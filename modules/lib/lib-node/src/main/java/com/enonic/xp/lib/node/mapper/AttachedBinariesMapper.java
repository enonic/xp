package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class AttachedBinariesMapper
    implements MapSerializable
{
    private final AttachedBinaries value;

    public AttachedBinariesMapper( final AttachedBinaries value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final AttachedBinaries binaries )
    {
        gen.array( "attachedBinaries" );

        binaries.forEach( ( binary ) -> {
            gen.map();
            gen.value( "binaryReference", binary.getBinaryReference().toString() );
            gen.value( "blobKey", binary.getBlobKey() );
            gen.end();
        } );

        gen.end();
    }
}
