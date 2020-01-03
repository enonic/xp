package com.enonic.xp.impl.server.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.index.UpdateIndexSettingsResult;

public final class UpdateIndexSettingsResultJson
{
    public List<String> updatedIndexes;

    public static UpdateIndexSettingsResultJson create( final UpdateIndexSettingsResult result )
    {
        final UpdateIndexSettingsResultJson json = new UpdateIndexSettingsResultJson();
        json.updatedIndexes = new ArrayList<>( result.getUpdatedIndexes() );
        return json;
    }
}
