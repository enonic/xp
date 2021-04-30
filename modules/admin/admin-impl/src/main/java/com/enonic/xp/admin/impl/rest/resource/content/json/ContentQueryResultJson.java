package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.JsonObjectsFactory;
import com.enonic.xp.content.Content;

public class ContentQueryResultJson
    extends AbstractContentQueryResultJson<ContentJson>
{
    public ContentQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentQueryResultJson.Builder newBuilder( final JsonObjectsFactory jsonObjectsFactory )
    {
        return new Builder( jsonObjectsFactory );
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private final JsonObjectsFactory jsonObjectsFactory;

        private final List<ContentJson> contents = new ArrayList<>();

        public Builder( final JsonObjectsFactory jsonObjectsFactory )
        {
            this.jsonObjectsFactory = jsonObjectsFactory;
        }

        @Override
        public Builder addContent( final Content content )
        {
            this.contents.add( jsonObjectsFactory.createContentJson( content ) );
            return this;
        }

        @Override
        public ContentQueryResultJson build()
        {
            return new ContentQueryResultJson( this );
        }
    }
}
