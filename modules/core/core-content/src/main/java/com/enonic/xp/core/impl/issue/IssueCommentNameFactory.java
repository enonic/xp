package com.enonic.xp.core.impl.issue;

public class IssueCommentNameFactory
{
    public static String ISSUE_COMMENT_NAME_PREFIX = "comment-";

    public static String create( final long index )
    {
        return ISSUE_COMMENT_NAME_PREFIX + index;
    }

}
