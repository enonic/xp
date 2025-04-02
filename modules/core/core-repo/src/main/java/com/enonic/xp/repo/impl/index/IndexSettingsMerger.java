package com.enonic.xp.repo.impl.index;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.core.internal.json.JsonHelper;

public final class IndexSettingsMerger
{
    private IndexSettingsMerger()
    {
    }

    public static IndexSettings merge( final IndexSettings first, final IndexSettings second )
    {
        if ( second == null )
        {
            return first;
        }
        final JsonNode firstJsonNode = JsonHelper.from( first.getData() );
        final JsonNode secondJsonNode = JsonHelper.from( second.getData() );
        return IndexSettings.from( JsonHelper.toMap( JsonHelper.merge( firstJsonNode, secondJsonNode ) ) );
    }

    public static IndexMapping merge( final IndexMapping first, final IndexMapping second )
    {
        if ( second == null )
        {
            return first;
        }
        final JsonNode firstJsonNode = JsonHelper.from( first.getData() );
        final JsonNode secondJsonNode = JsonHelper.from( second.getData() );
        return IndexMapping.from( JsonHelper.toMap( JsonHelper.merge( firstJsonNode, secondJsonNode ) ) );
    }
}
