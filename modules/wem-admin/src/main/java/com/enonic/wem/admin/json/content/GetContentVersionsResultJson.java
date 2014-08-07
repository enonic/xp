package com.enonic.wem.admin.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersions;

public class GetContentVersionsResultJson
{
    private Set<ContentVersionJson> contentVersions = Sets.newLinkedHashSet();

    public GetContentVersionsResultJson( final ContentVersions contentVersions )
    {
        for ( final ContentVersion contentVersion : contentVersions )
        {
            this.contentVersions.add( new ContentVersionJson( contentVersion ) );
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<ContentVersionJson> getContentVersions()
    {
        return contentVersions;
    }
}
