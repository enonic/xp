package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.security.PrincipalKey;

public class Comment
{
    private PrincipalKey creator;

    private Instant createdTime;

    private String text;

    public Comment( final PrincipalKey creator, final String text, final Instant createdTime )
    {
        this.creator = creator;
        this.createdTime = createdTime;
        this.text = text;
    }

    public Comment( final PrincipalKey creator, final String text )
    {
        this( creator, text, Instant.now() );
    }

    public Comment( final PrincipalKey creator )
    {
        this( creator, "" );
    }

    public PrincipalKey getCreator()
    {
        return creator;
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
