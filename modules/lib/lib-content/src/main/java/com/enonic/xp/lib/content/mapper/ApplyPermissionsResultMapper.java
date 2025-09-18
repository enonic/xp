package com.enonic.xp.lib.content.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ApplyPermissionsResultMapper
    implements MapSerializable
{
    private final ApplyContentPermissionsResult result;


    public ApplyPermissionsResultMapper( final ApplyContentPermissionsResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Map.Entry<ContentId, List<ApplyContentPermissionsResult.BranchResult>> entry : result.getResults().entrySet() )
        {
            gen.map( entry.getKey().toString() );

            gen.array( "branchResults" );
            entry.getValue().forEach( branchResult -> {
                gen.map();
                gen.value( "branch", branchResult.branch() );
                gen.value( "content", new PermissionsMapper( branchResult.content() ) );
                gen.end();
            } );
            gen.end();
            gen.end();
        }
    }
}
