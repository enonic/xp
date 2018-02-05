package com.enonic.xp.admin.impl.json.issue;

import java.time.Instant;

import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.security.PrincipalKey;

public class IssueCommentJson
{
    public final NodeName name;

    public final PrincipalKey creatorKey;

    public final String creatorDisplayName;

    public final Instant createdTime;

    public final String text;

    public IssueCommentJson( final IssueComment comment )
    {
        this.name = comment.getName();
        this.creatorKey = comment.getCreator();
        this.creatorDisplayName = comment.getCreatorDisplayName();
        this.createdTime = comment.getCreated();
        this.text = comment.getText();
    }

    public String getName()
    {
        return name != null ? name.toString() : null;
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
