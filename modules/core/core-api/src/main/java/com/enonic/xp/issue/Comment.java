package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.security.PrincipalKey;

public class Comment
{
    private PrincipalKey creatorKey;

    private String creatorDisplayName;

    private Instant createdTime;

    private String text;

    public Comment( final PrincipalKey creatorKey, final String creatorDisplayName, final String text, final Instant createdTime )
    {
        this.creatorKey = creatorKey;
        this.creatorDisplayName = creatorDisplayName;
        this.createdTime = createdTime;
        this.text = text;
    }

    public Comment( final PrincipalKey creatorKey, final String creatorDisplayName, final String text )
    {
        this( creatorKey, creatorDisplayName, text, Instant.now() );
    }

    public Comment( final PrincipalKey creatorKey, final String creatorDisplayName )
    {
        this( creatorKey, creatorDisplayName, "" );
    }

    public PrincipalKey getCreatorKey()
    {
        return creatorKey;
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
