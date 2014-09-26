package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

public class ContentQueryResultJson
    extends AbstractContentQueryResultJson<ContentJson>
{
    public ContentQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentQueryResultJson.Builder newBuilder( final ContentIconUrlResolver iconUrlResolver )
    {
        return new Builder( iconUrlResolver );
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private final ContentIconUrlResolver iconUrlResolver;

        private Set<ContentJson> contents = Sets.newLinkedHashSet();

        public Builder( final ContentIconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
        }

        public Builder addContent( final Content content,
                                   final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer )
        {
            this.contents.add( new ContentJson( content, iconUrlResolver, mixinReferencesToFormItemsTransformer ) );
            return this;
        }

        public ContentQueryResultJson build()
        {
            return new ContentQueryResultJson( this );
        }

    }


}
