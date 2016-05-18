package com.enonic.xp.admin.impl.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;

public class UnpublishContentResultJson
{
    private final Set<Success> successes = Sets.newHashSet();

    public UnpublishContentResultJson( final Contents contents )
    {
        for ( final Content content : contents )
        {
            this.successes.add( new Success( content.getId(), content.getDisplayName() ) );
        }
    }

    public Set<Success> getSuccesses()
    {
        return successes;
    }

    public static class Success
    {
        private final String id;

        private final String name;

        public Success( final ContentId contentId, final String displayName )
        {
            this.id = contentId.toString();
            this.name = displayName;
        }

        public String getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }
    }
}
