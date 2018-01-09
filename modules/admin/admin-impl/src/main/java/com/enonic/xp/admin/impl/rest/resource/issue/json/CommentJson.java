package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.time.Instant;

import com.enonic.xp.issue.Comment;
import com.enonic.xp.security.PrincipalKey;

public class CommentJson
{

    public String creatorKey;

    public String creatorDisplayName;

    public Instant createdTime;

    public String text;

    public CommentJson( final String creatorKey, final String creatorDisplayName, final String text, final String createdTime )
    {
        this.creatorKey = creatorKey;
        this.creatorDisplayName = creatorDisplayName;
        this.createdTime = createdTime != null ? Instant.parse( createdTime ) : Instant.now();
        this.text = text;
    }

    public Comment toComment()
    {
        return new Comment( PrincipalKey.from( this.creatorKey ), this.creatorDisplayName, this.text, this.createdTime );
    }

    public static CommentJson from( Comment comment )
    {
        return new CommentJson( comment.getCreatorKey().toString(), comment.getCreatorDisplayName(), comment.getText(),
                                comment.getCreatedTime().toString() );
    }
}
