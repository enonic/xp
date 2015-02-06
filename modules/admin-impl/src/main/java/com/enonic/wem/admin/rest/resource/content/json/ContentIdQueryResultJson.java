package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.form.InlinesToFormItemsTransformer;

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

        public Builder addContent( final Content content,
                                   final InlinesToFormItemsTransformer inlinesToFormItemsTransformer )
        {
            this.contents.add( new ContentIdJson( content.getId() ) );
            return this;
        }

        public ContentIdQueryResultJson build()
        {
            return new ContentIdQueryResultJson( this );
        }
    }
}
