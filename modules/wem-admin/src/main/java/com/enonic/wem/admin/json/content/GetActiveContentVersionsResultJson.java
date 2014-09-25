package com.enonic.wem.admin.json.content;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.GetActiveContentVersionsResult;
import com.enonic.wem.api.entity.Workspace;

public class GetActiveContentVersionsResultJson
{
    public final Map<String, ContentVersionJson> activeContentVersions;

    public GetActiveContentVersionsResultJson( final GetActiveContentVersionsResult result )
    {
        this.activeContentVersions = Maps.newLinkedHashMap();

        for ( final Workspace workspace : result.getContentVersions().keySet() )
        {
            this.activeContentVersions.put( workspace.getName(), new ContentVersionJson( result.getContentVersions().get( workspace ) ) );
        }
    }

    @SuppressWarnings("unusedDeclaration")
    public Map<String, ContentVersionJson> getActiveContentVersions()
    {
        return activeContentVersions;
    }
}
