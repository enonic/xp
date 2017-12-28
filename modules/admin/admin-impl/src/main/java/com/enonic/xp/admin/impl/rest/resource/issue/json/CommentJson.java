package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.time.Instant;

import com.enonic.xp.issue.Comment;
import com.enonic.xp.security.PrincipalKey;

public class CommentJson
{

    public String creator;

    public Instant createdTime;

    public String text;

    public CommentJson( final String creator, final String text, final String createdTime )
    {
        this.creator = creator;
        this.createdTime = createdTime != null ? Instant.parse( createdTime ) : Instant.now();
        this.text = text;
    }

    public Comment toComment()
    {
        return new Comment( PrincipalKey.from( this.creator ), this.text, this.createdTime );
    }

    public static CommentJson from( Comment comment )
    {
        return new CommentJson( comment.getCreator().toString(), comment.getText(), comment.getCreatedTime().toString() );
    }
}
