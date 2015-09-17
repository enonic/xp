package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.content.Content;

public class ContentIdQueryResultJson
    extends AbstractContentQueryResultJson<ContentIdJson>
{
    public ContentIdQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentIdQueryResultJson.Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private Set<ContentIdJson> contents = Sets.newLinkedHashSet();

        @Override
        public Builder addContent( final Content content )
        {
            this.contents.add( new ContentIdJson( content.getId() ) );
            return this;
        }

        @Override
        public ContentIdQueryResultJson build()
        {
            return new ContentIdQueryResultJson( this );
        }
    }
}
