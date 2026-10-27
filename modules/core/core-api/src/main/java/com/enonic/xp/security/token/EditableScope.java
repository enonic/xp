package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.PrincipalKeys;

/**
 * A scope as a {@link ScopeEditor} sees it: every value that may change, as a field, starting out
 * as the scope has it. The id provider and the id do not change.
 */
@NullMarked
public final class EditableScope
{
    /**
     * The scope as it is before the edit.
     */
    public final Scope source;

    /**
     * The title; null falls back to the id.
     */
    public @Nullable String title;

    /**
     * The description; null leaves the scope without one.
     */
    public @Nullable String description;

    /**
     * Roles only; empty makes the scope a label.
     */
    public PrincipalKeys roles;

    /**
     * Whether every token of the id provider carries the scope.
     */
    public boolean required;

    /**
     * Starts out with the values of the scope.
     */
    public EditableScope( final Scope source )
    {
        this.source = source;
        this.title = source.getTitle();
        this.description = source.getDescription();
        this.roles = source.getRoles();
        this.required = source.isRequired();
    }

    /**
     * @throws IllegalArgumentException when a principal is not a role
     */
    public Scope build()
    {
        return Scope.create()
            .idProvider( source.getIdProvider() )
            .id( source.getId() )
            .title( title )
            .description( description )
            .roles( roles )
            .required( required )
            .build();
    }
}
