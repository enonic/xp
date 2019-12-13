package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.Content;

public class ContentQueryResultJson
    extends AbstractContentQueryResultJson<ContentJson>
{
    public ContentQueryResultJson( final Builder builder )
    {
        super( builder );
        this.contents = ImmutableSet.copyOf( builder.contents );
    }

    public static ContentQueryResultJson.Builder newBuilder( final ContentIconUrlResolver iconUrlResolver,
                                                             final ContentPrincipalsResolver contentPrincipalsResolver,
                                                             final ComponentNameResolver componentNameResolver )
    {
        return new Builder( iconUrlResolver, contentPrincipalsResolver, componentNameResolver );
    }

    public static class Builder
        extends AbstractContentQueryResultJson.Builder<Builder>
    {
        private final ContentIconUrlResolver iconUrlResolver;

        private final ContentPrincipalsResolver contentPrincipalsResolver;

        private final ComponentNameResolver componentNameResolver;

        private Set<ContentJson> contents = new LinkedHashSet<>();

        public Builder( final ContentIconUrlResolver iconUrlResolver, final ContentPrincipalsResolver contentPrincipalsResolver,
                        final ComponentNameResolver componentNameResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            this.contentPrincipalsResolver = contentPrincipalsResolver;
            this.componentNameResolver = componentNameResolver;
        }

        @Override
        public Builder addContent( final Content content )
        {
            this.contents.add( new ContentJson( content, iconUrlResolver, contentPrincipalsResolver, componentNameResolver ) );
            return this;
        }

        @Override
        public ContentQueryResultJson build()
        {
            return new ContentQueryResultJson( this );
        }

    }


}
