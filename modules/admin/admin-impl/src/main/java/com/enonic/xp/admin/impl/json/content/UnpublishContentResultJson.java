package com.enonic.xp.admin.impl.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

public class UnpublishContentResultJson
{
    private final Set<ContentIdJson> contentIds = Sets.newHashSet();

    public UnpublishContentResultJson( final ContentIds contentIds )
    {
        for ( final ContentId contentId : contentIds )
        {
            this.contentIds.add( new ContentIdJson( contentId ) );
        }
    }

    public Set<ContentIdJson> getContentIds()
    {
        return contentIds;
    }
}
