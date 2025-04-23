package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PatchNodeResultMapper
    implements MapSerializable
{
    private final PatchNodeResult result;

    public PatchNodeResultMapper( final PatchNodeResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "branchResults" );
        for ( final PatchNodeResult.BranchResult branchResult : result.getResults() )
        {
            gen.map();
            gen.value( "branch", branchResult.branch() );
            gen.value( "node", new NodeMapper( branchResult.node() ) );
            gen.end();
        }
        gen.end();
    }


}
