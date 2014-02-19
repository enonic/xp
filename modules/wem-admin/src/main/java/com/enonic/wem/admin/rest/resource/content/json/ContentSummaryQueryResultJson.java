package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.api.content.Content;

public class ContentSummaryQueryResultJson
    extends AbstractContentQueryResultJson<ContentSummaryJson>
{
    public ContentSummaryQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentSummaryQueryResultJson.Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private Set<ContentSummaryJson> contents = Sets.newLinkedHashSet();

        public Builder addContent( final Content content )
        {
            this.contents.add( new ContentSummaryJson( content ) );
            return this;
        }

        public ContentSummaryQueryResultJson build()
        {
            return new ContentSummaryQueryResultJson( this );
        }

    }
}
