package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.Content;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;

public class ContentQueryResultJson
    extends AbstractContentQueryResultJson<ContentJson>
{
    public ContentQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentQueryResultJson.Builder newBuilder( final ContentIconUrlResolver iconUrlResolver,
                                                             final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        return new Builder( iconUrlResolver, contentPrincipalsResolver );
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private final ContentIconUrlResolver iconUrlResolver;

        private final ContentPrincipalsResolver contentPrincipalsResolver;

        private Set<ContentJson> contents = Sets.newLinkedHashSet();

        public Builder( final ContentIconUrlResolver iconUrlResolver, final ContentPrincipalsResolver contentPrincipalsResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            this.contentPrincipalsResolver = contentPrincipalsResolver;
        }

        @Override
        public Builder addContent( final Content content,
                                   final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer )
        {
            this.contents.add(
                new ContentJson( content, iconUrlResolver, inlineMixinsToFormItemsTransformer, contentPrincipalsResolver ) );
            return this;
        }

        @Override
        public ContentQueryResultJson build()
        {
            return new ContentQueryResultJson( this );
        }

    }


}
