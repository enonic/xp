package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.IssueName;

public class IssueNameFactory
{
    public static String ISSUE_NAME_PREFIX = "issue-";

    public static IssueName create( final long index )
    {
        return IssueName.from( ISSUE_NAME_PREFIX + index );
    }

}
