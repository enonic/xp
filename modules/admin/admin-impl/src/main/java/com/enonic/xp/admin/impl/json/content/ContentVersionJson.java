package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.security.Principal;

public class ContentVersionJson
{
    private final String modifier;

    private final String modifierDisplayName;

    private final String displayName;

    private final Instant modified;

    private final String comment;

    private final String id;

    public ContentVersionJson( final ContentVersion contentVersion, final Principal modifier )
    {
        this.modified = contentVersion.getModified();
        this.displayName = contentVersion.getDisplayName();
        this.comment = contentVersion.getComment();
        this.modifier = contentVersion.getModifier().toString();
        this.modifierDisplayName = modifier == null ? "" : modifier.getDisplayName();
        this.id = contentVersion.getId().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getModifier()
    {
        return modifier;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getDisplayName()
    {
        return displayName;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getModified()
    {
        return modified;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getComment()
    {
        return comment;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getModifierDisplayName()
    {
        return modifierDisplayName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ContentVersionJson that = (ContentVersionJson) o;
        return Objects.equals( modifier, that.modifier ) && Objects.equals( modifierDisplayName, that.modifierDisplayName ) &&
            Objects.equals( displayName, that.displayName ) && Objects.equals( modified, that.modified ) &&
            Objects.equals( comment, that.comment ) && Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( modifier, modifierDisplayName, displayName, modified, comment, id );
    }
}
