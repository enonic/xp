package com.enonic.xp.content;

import static java.util.Objects.requireNonNull;

/**
 * One content in a {@link ListContentsByParentResult}, identified without reading the content itself.
 *
 * @since 8.1.0
 */
public record ContentListEntry(ContentId id, ContentPath path)
{
    public ContentListEntry
    {
        requireNonNull( id, "id is required" );
        requireNonNull( path, "path is required" );
    }
}
