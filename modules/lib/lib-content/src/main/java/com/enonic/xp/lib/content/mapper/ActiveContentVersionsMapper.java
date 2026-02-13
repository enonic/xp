package com.enonic.xp.lib.content.mapper;

import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ActiveContentVersionsMapper
    implements MapSerializable
{
    private final GetActiveContentVersionsResult result;

    public ActiveContentVersionsMapper( final GetActiveContentVersionsResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Map.Entry<Branch, ContentVersion> entry : result.getContentVersions().entrySet() )
        {
            gen.map( entry.getKey().getValue() );
            new ContentVersionMapper( entry.getValue() ).serialize( gen );
            gen.end();
        }
    }
}
