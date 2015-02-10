package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.form.InlinesToFormItemsTransformer;

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

        public Builder addContent( final Content content,
                                   final InlinesToFormItemsTransformer inlinesToFormItemsTransformer )
        {
            this.contents.add(
                new ContentJson( content, iconUrlResolver, inlinesToFormItemsTransformer, contentPrincipalsResolver ) );
            return this;
        }

        public ContentQueryResultJson build()
        {
            return new ContentQueryResultJson( this );
        }

    }


}
