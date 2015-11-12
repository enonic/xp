package com.enonic.xp.admin.impl.rest.resource.repo;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.index.UpdateIndexSettingsResult;

public final class UpdateIndexSettingsResultJson
{
    public List<String> updatedIndexes;

    public static UpdateIndexSettingsResultJson create( final UpdateIndexSettingsResult result )
    {
        final UpdateIndexSettingsResultJson json = new UpdateIndexSettingsResultJson();
        json.updatedIndexes = Lists.newArrayList( result.getUpdatedIndexes() );
        return json;
    }
}
