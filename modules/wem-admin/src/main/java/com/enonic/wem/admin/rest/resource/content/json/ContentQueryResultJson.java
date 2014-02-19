package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.api.content.Content;

public class ContentQueryResultJson
    extends AbstractContentQueryResultJson<ContentJson>
{
    public ContentQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentQueryResultJson.Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private Set<ContentJson> contents = Sets.newLinkedHashSet();

        public Builder addContent( final Content content )
        {
            this.contents.add( new ContentJson( content ) );
            return this;
        }

        public ContentQueryResultJson build()
        {
            return new ContentQueryResultJson( this );
        }

    }


}
