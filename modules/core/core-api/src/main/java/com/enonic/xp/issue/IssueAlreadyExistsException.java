package com.enonic.xp.issue;

public final class IssueAlreadyExistsException
    extends RuntimeException
{
    public IssueAlreadyExistsException( final IssueName issueName )
    {
        super( "Issue with same name already exist, name: " + issueName );
    }
}
