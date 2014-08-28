package com.enonic.wem.admin.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentVersion;
import com.enonic.wem.api.content.ContentVersions;

public class ContentVersionsJson
{
    private Set<ContentVersionJson> contentVersions = Sets.newLinkedHashSet();

    public ContentVersionsJson( final ContentVersions contentVersions )
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
