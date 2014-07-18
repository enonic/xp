package com.enonic.wem.admin.json.content;

import java.time.Instant;

import com.enonic.wem.api.content.versioning.ContentVersion;

public class ContentVersionJson
{
    private final String modifier;

    private final String displayName;

    private final Instant modified;

    private final String comment;

    public ContentVersionJson( final ContentVersion contentVersion )
    {
        this.modified = contentVersion.getModified();
        this.displayName = contentVersion.getDisplayName();
        this.comment = contentVersion.getComment();
        this.modifier = contentVersion.getModifier().getQualifiedName();
    }
}
