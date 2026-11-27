package com.enonic.xp.security.token;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;

/**
 * A scope: a named area of access that XP or an application defines. It is what a client may ask
 * for and what a user is asked to approve; an API says which scopes it accepts, and a token is let
 * in when it carries one of them. A scope may include others, as a wider level of access includes
 * the narrower ones.
 */
@NullMarked
public final class Scope
{
    private final String id;

    private final String title;

    private final @Nullable String description;

    private final @Nullable ApplicationKey application;

    private final @Nullable String titleI18nKey;

    private final @Nullable String descriptionI18nKey;

    private final ImmutableSet<String> includes;

    private final boolean required;

    private Scope( final Builder builder )
    {
        this.id = ScopeNames.checkScope( requireNonNull( builder.id, "id is required" ) );
        this.title = builder.title == null ? this.id : builder.title;
        this.description = builder.description;
        this.application = builder.application;
        this.titleI18nKey = builder.titleI18nKey;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.includes = ImmutableSet.copyOf( builder.includes );
        this.includes.forEach( ScopeNames::checkScope );
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
     * The scope's id: what a client puts in {@code scope} and what the token carries. A
     * {@link ScopeNames scope name}: a plain name for a scope of XP's, {@code <application>:<name>} for an
     * application's.
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
     * The ids of the scopes this one includes: a token granted this scope passes wherever one of
     * them is accepted.
     */
    public Set<String> getIncludes()
    {
        return includes;
    }

    /**
     * Whether the user may not drop the scope when approving a request that names it.
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * The application that defines the scope, whose phrases the i18n keys name; null for a scope of XP's.
     */
    public @Nullable ApplicationKey getApplication()
    {
        return application;
    }

    /**
     * The key of the title's phrase among the application's, or null. {@link #getTitle()} is the
     * text to show where there is no such phrase.
     */
    public @Nullable String getTitleI18nKey()
    {
        return titleI18nKey;
    }

    /**
     * The key of the description's phrase among the application's, or null.
     */
    public @Nullable String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Scope that && id.equals( that.id ) &&
            title.equals( that.title ) && Objects.equals( description, that.description ) && includes.equals( that.includes ) &&
            required == that.required && Objects.equals( application, that.application ) &&
            Objects.equals( titleI18nKey, that.titleI18nKey ) && Objects.equals( descriptionI18nKey, that.descriptionI18nKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, title, description, includes, required, application, titleI18nKey, descriptionI18nKey );
    }

    @Override
    public String toString()
    {
        return "Scope{" + "id=" + id + ", title=" + title + ", description=" + description +
            ", includes=" + includes + ", required=" + required + '}';
    }

    /**
     * Builds a scope. Each value replaces the previous.
     */
    public static final class Builder
    {
        private @Nullable String id;

        private @Nullable String title;

        private @Nullable String description;

        private @Nullable ApplicationKey application;

        private @Nullable String titleI18nKey;

        private @Nullable String descriptionI18nKey;

        private Set<String> includes = Set.of();

        private boolean required;

        private Builder()
        {
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
         * The ids of the scopes this one includes; none by default.
         */
        public Builder includes( final Collection<String> includes )
        {
            this.includes = Set.copyOf( includes );
            return this;
        }

        /**
         * The application that declares the scope, whose phrases the i18n keys name.
         */
        public Builder application( final @Nullable ApplicationKey application )
        {
            this.application = application;
            return this;
        }

        /**
         * The key of the title's phrase among the application's.
         */
        public Builder titleI18nKey( final @Nullable String titleI18nKey )
        {
            this.titleI18nKey = titleI18nKey;
            return this;
        }

        /**
         * The key of the description's phrase among the application's.
         */
        public Builder descriptionI18nKey( final @Nullable String descriptionI18nKey )
        {
            this.descriptionI18nKey = descriptionI18nKey;
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
         * @throws NullPointerException     when the id is missing
         * @throws IllegalArgumentException when the id is not a scope name
         */
        public Scope build()
        {
            return new Scope( this );
        }
    }
}
