package com.enonic.xp.issue;

import com.enonic.xp.exception.DuplicateElementException;

public final class IssueAlreadyExistsException
    extends DuplicateElementException
{
    public IssueAlreadyExistsException( final IssueName issueName )
    {
        super( "Issue with same name already exist, name: " + issueName );
    }
}
