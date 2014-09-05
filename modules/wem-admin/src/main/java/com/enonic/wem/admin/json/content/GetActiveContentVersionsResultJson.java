package com.enonic.wem.admin.json.content;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.ContentVersion;
import com.enonic.wem.api.content.GetActiveContentVersionsResult;
import com.enonic.wem.api.entity.Workspace;

public class GetActiveContentVersionsResultJson
{
    public final Map<String, ContentVersionJson> activeContentVersions;


    public GetActiveContentVersionsResultJson( final GetActiveContentVersionsResult result )
    {
        this.activeContentVersions = Maps.newHashMap();

        final ImmutableMap<Workspace, ContentVersion> contentVersionsMap = result.getContentVersions();

        for ( final Workspace workspace : contentVersionsMap.keySet() )
        {
            this.activeContentVersions.put( workspace.getName(), new ContentVersionJson( contentVersionsMap.get( workspace ) ) );
        }
    }

    public Map<String, ContentVersionJson> getActiveContentVersions()
    {
        return activeContentVersions;
    }
}
