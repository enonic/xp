package com.enonic.xp.app;

import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.schema.LocalizedText;

import static java.util.Objects.requireNonNull;

/**
 * A scope as an application descriptor defines it, under {@code scopes}. The scope a token carries
 * is named {@code <application>:<name>}.
 *
 * @param name        the scope's name within the application
 * @param title       a short title for the consent page, as text or as text with an i18n key, or null
 *                    for the scope's id
 * @param description what approving the scope allows, in the user's words, as text or as text with an
 *                    i18n key, or null
 * @param includes    the names of the application's scopes this one includes, or null for none
 * @param required    whether the user may not drop the scope when approving a request that names it
 */
@NullMarked
public record ApplicationScope(String name, @Nullable LocalizedText title, @Nullable LocalizedText description,
                               @Nullable List<String> includes, boolean required)
{
    public ApplicationScope
    {
        requireNonNull( name, "name is required" );
        includes = includes == null ? List.of() : List.copyOf( includes );
    }

    /**
     * A scope the user may drop, with the title and description defaulted, including the named ones.
     */
    public ApplicationScope( final String name, final String... includes )
    {
        this( name, null, null, List.of( includes ), false );
    }
}
