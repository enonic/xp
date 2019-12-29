package com.enonic.xp.issue;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public final class IssueNotFoundException
    extends NotFoundException
{
    public IssueNotFoundException( final IssueId id )
    {
        super( MessageFormat.format( "Issue with id [{0}] was not found", id.toString() ) );
    }
}
