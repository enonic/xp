package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.UpdateMetadataResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class UpdateMetadataResultMapper
    implements MapSerializable
{
    private final UpdateMetadataResult value;

    public UpdateMetadataResultMapper( final UpdateMetadataResult value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "contentId", value.getContentId().toString() );
        serializeBranchResults( gen );
    }


    private void serializeBranchResults( final MapGenerator gen )
    {
        gen.array( "results" );
        for ( UpdateMetadataResult.BranchResult result : value.getResults() )
        {
            gen.map();
            gen.value( "branch", result.branch().getValue() );
            gen.value( "content", result.content() != null ? new ContentMapper( result.content() ) : null );
            gen.end();
        }
        gen.end();
    }
}
