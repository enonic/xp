package com.enonic.xp.issue;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public final class IssueNotFoundException
    extends NotFoundException
{
    public IssueNotFoundException( final IssueId id )
    {
        super( MessageFormat.format( "Issue with id [{0}] was not found", id.toString() ) );
    }
}
