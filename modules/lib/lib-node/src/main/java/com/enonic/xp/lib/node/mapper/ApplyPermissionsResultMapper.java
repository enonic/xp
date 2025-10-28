package com.enonic.xp.lib.node.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ApplyPermissionsResultMapper
    implements MapSerializable
{
    private final ApplyNodePermissionsResult result;


    public ApplyPermissionsResultMapper( final ApplyNodePermissionsResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Map.Entry<NodeId, List<ApplyNodePermissionsResult.BranchResult>> entry : result.getResults().entrySet() )
        {
            gen.map( entry.getKey().toString() );

            gen.array( "branchResults" );
            entry.getValue().forEach( branchResult -> {
                gen.map();
                gen.value( "branch", branchResult.branch() );
                gen.value( "permissions", new PermissionsMapper( branchResult.permissions() ) );
                gen.end();
            } );
            gen.end();
            gen.end();
        }
    }
}
