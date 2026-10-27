package com.enonic.xp.security.token;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A scope of an id provider: what a client may ask for, what a user is asked to approve, and the
 * roles a token granted it is restricted to. A scope with no roles restricts nothing.
 */
@NullMarked
public final class Scope
{
    private final IdProviderKey idProvider;

    private final String id;

    private final String title;

    private final @Nullable String description;

    private final PrincipalKeys roles;

    private final boolean required;

    private Scope( final Builder builder )
    {
        this.idProvider = requireNonNull( builder.idProvider, "idProvider is required" );
        this.id = ScopeNames.checkScope( requireNonNull( builder.id, "id is required" ) );
        this.title = builder.title == null ? this.id : builder.title;
        this.description = builder.description;
        this.roles = builder.roles;
        for ( final PrincipalKey role : this.roles )
        {
            checkArgument( role.isRole(), "a scope may only imply roles, not %s", role );
        }
        this.required = builder.required;
    }

    /**
     * A builder for a scope.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The id provider the scope belongs to.
     */
    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    /**
     * The scope's id: what a client puts in {@code scope} and what the token carries. A
     * {@link ScopeNames scope name}, unique within the id provider.
     */
    public String getId()
    {
        return id;
    }

    /**
     * A short title for the consent page. The id when none was given.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * What approving the scope allows, in the user's words, or null.
     */
    public @Nullable String getDescription()
    {
        return description;
    }

    /**
     * The roles a token granted this scope is restricted to. Empty for a label.
     */
    public PrincipalKeys getRoles()
    {
        return roles;
    }

    /**
     * Whether the user may not drop the scope when approving a request that names it.
     */
    public boolean isRequired()
    {
        return required;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Scope that && idProvider.equals( that.idProvider ) && id.equals( that.id ) &&
            title.equals( that.title ) && Objects.equals( description, that.description ) && roles.equals( that.roles ) &&
            required == that.required;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( idProvider, id, title, description, roles, required );
    }

    @Override
    public String toString()
    {
        return "Scope{" + "idProvider=" + idProvider + ", id=" + id + ", title=" + title + ", description=" + description +
            ", roles=" + roles + ", required=" + required + '}';
    }

    /**
     * Builds a scope. Each value replaces the previous.
     */
    public static final class Builder
    {
        private @Nullable IdProviderKey idProvider;

        private @Nullable String id;

        private @Nullable String title;

        private @Nullable String description;

        private PrincipalKeys roles = PrincipalKeys.empty();

        private boolean required;

        private Builder()
        {
        }

        /**
         * The id provider the scope belongs to. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * The scope's id. Required; a {@link ScopeNames scope name}.
         */
        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        /**
         * The title shown on the consent page; the id when null.
         */
        public Builder title( final @Nullable String title )
        {
            this.title = title;
            return this;
        }

        /**
         * What the scope allows, in the user's words.
         */
        public Builder description( final @Nullable String description )
        {
            this.description = description;
            return this;
        }

        /**
         * The roles the scope implies; empty (the default) for a label.
         */
        public Builder roles( final PrincipalKeys roles )
        {
            this.roles = roles;
            return this;
        }

        /**
         * Whether the user may not drop the scope; false by default.
         */
        public Builder required( final boolean required )
        {
            this.required = required;
            return this;
        }

        /**
         * Builds the scope.
         *
         * @throws NullPointerException     when the id provider or the id is missing
         * @throws IllegalArgumentException when the id is not a scope name, or a principal is not a role
         */
        public Scope build()
        {
            return new Scope( this );
        }
    }
}
