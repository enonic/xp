package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.security.Principal;

public class ContentVersionJson
{
    private final String modifier;

    private final String modifierDisplayName;

    private final String displayName;

    private final Instant modified;

    private final Instant timestamp;

    private final String comment;

    private final String id;

    private final ContentVersionPublishInfoJson publishInfo;

    private final ContentWorkflowInfoJson workflow;

    public ContentVersionJson( final ContentVersion contentVersion, final ContentPrincipalsResolver principalsResolver )
    {
        this.modified = contentVersion.getModified();
        this.timestamp = contentVersion.getTimestamp();
        this.displayName = contentVersion.getDisplayName();
        this.comment = contentVersion.getComment();

        final Principal modifier = principalsResolver.findPrincipal( contentVersion.getModifier() );

        this.modifierDisplayName = modifier != null ? modifier.getDisplayName() : "";
        this.modifier = contentVersion.getModifier().toString();
        this.id = contentVersion.getId().toString();
        this.publishInfo = contentVersion.getPublishInfo() != null ? new ContentVersionPublishInfoJson( contentVersion.getPublishInfo(),
                                                                                                        principalsResolver ) : null;

        this.workflow = contentVersion.getWorkflowInfo() != null ? new ContentWorkflowInfoJson( contentVersion.getWorkflowInfo() ) : null;

    }

    @SuppressWarnings("UnusedDeclaration")
    public String getModifier()
    {
        return modifier;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getTimestamp()
    {
        return timestamp;
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

    @SuppressWarnings("unused")
    public ContentVersionPublishInfoJson getPublishInfo()
    {
        return publishInfo;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ContentWorkflowInfoJson getWorkflow()
    {
        return workflow;
    }
}
