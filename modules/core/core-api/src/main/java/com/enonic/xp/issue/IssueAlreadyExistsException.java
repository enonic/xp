package com.enonic.xp.issue;

public class IssueAlreadyExistsException
    extends RuntimeException
{
    public IssueAlreadyExistsException( final IssueName issueName )
    {
        super( "Issue with same name already exist, name: " + issueName );
    }
}
