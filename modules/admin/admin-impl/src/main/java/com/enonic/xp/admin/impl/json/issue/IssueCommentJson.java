package com.enonic.xp.admin.impl.json.issue;

import java.time.Instant;

import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;

public class IssueCommentJson
{
    private final NodeId id;

    private final PrincipalKey creatorKey;

    private final String creatorDisplayName;

    private final Instant createdTime;

    private final String text;

    public IssueCommentJson( final IssueComment comment )
    {
        this.id = comment.getId();
        this.creatorKey = comment.getCreator();
        this.creatorDisplayName = comment.getCreatorDisplayName();
        this.createdTime = comment.getCreated();
        this.text = comment.getText();
    }

    public String getId()
    {
        return id != null ? id.toString() : null;
    }

    public String getCreatorKey()
    {
        return creatorKey != null ? creatorKey.toString() : null;
    }

    public String getCreatorDisplayName()
    {
        return creatorDisplayName;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public String getText()
    {
        return text;
    }
}
