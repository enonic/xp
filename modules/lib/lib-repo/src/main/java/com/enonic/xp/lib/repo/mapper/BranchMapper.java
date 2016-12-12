package com.enonic.xp.lib.repo.mapper;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class BranchMapper
    implements MapSerializable
{
    private Branch branch;

    public BranchMapper( final Branch branch )
    {
        this.branch = branch;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "id", branch.getValue() );
    }
}
