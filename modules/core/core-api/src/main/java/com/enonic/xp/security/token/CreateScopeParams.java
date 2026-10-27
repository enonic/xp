package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;

/**
 * A scope to add to an id provider. The same values as the {@link Scope} it creates.
 */
@NullMarked
public final class CreateScopeParams
{
    private final Scope scope;

    private CreateScopeParams( final Builder builder )
    {
        this.scope = builder.scope.build();
    }

    /**
     * A builder for the parameters.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The scope as it will be stored.
     */
    public Scope getScope()
    {
        return scope;
    }

    /**
     * Collects the scope's values; {@link #build()} checks them.
     */
    public static final class Builder
    {
        private final Scope.Builder scope = Scope.create();

        private Builder()
        {
        }

        /**
         * The id provider to add the scope to. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            scope.idProvider( idProvider );
            return this;
        }

        /**
         * The scope's id. Required; a {@link Scopes scope name}.
         */
        public Builder id( final String id )
        {
            scope.id( id );
            return this;
        }

        /**
         * The title shown on the consent page; the id when null.
         */
        public Builder title( final @Nullable String title )
        {
            scope.title( title );
            return this;
        }

        /**
         * What the scope allows, in the user's words.
         */
        public Builder description( final @Nullable String description )
        {
            scope.description( description );
            return this;
        }

        /**
         * The roles the scope implies; empty (the default) for a label.
         */
        public Builder roles( final PrincipalKeys roles )
        {
            scope.roles( roles );
            return this;
        }

        /**
         * Whether the user may not drop the scope; false by default.
         */
        public Builder required( final boolean required )
        {
            scope.required( required );
            return this;
        }

        /**
         * @throws NullPointerException     when the id provider or the id is missing
         * @throws IllegalArgumentException when the id is not a scope name, or a principal is not a role
         */
        public CreateScopeParams build()
        {
            return new CreateScopeParams( this );
        }
    }
}
