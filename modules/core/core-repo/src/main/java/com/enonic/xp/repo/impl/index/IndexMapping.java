package com.enonic.xp.repo.impl.index;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.core.internal.json.JsonHelper;

public final class IndexMapping
{
    private final JsonNode jsonNode;

    private IndexMapping( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode;
    }

    public static IndexMapping from( final Map<String, ?> data )
    {
        return new IndexMapping( JsonHelper.from( data ) );
    }

    public Map<String, Object> getData()
    {
        return JsonHelper.toMap( jsonNode );
    }
}
