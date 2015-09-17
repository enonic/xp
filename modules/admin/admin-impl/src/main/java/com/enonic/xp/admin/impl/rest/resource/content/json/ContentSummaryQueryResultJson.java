package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.admin.impl.json.content.ContentSummaryJson;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.content.Content;

public class ContentSummaryQueryResultJson
    extends AbstractContentQueryResultJson<ContentSummaryJson>
{
    public ContentSummaryQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentSummaryQueryResultJson.Builder newBuilder( final ContentIconUrlResolver iconUrlResolver )
    {
        return new Builder( iconUrlResolver );
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private final ContentIconUrlResolver iconUrlResolver;

        private Set<ContentSummaryJson> contents = Sets.newLinkedHashSet();

        public Builder( final ContentIconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
        }

        @Override
        public Builder addContent( final Content content )
        {
            this.contents.add( new ContentSummaryJson( content, iconUrlResolver ) );
            return this;
        }

        @Override
        public ContentSummaryQueryResultJson build()
        {
            return new ContentSummaryQueryResultJson( this );
        }

    }
}
