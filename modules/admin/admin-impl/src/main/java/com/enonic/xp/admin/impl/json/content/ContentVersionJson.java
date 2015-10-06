package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

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
}
