package com.enonic.xp.core.impl.issue;

import java.time.Instant;

public class IssueCommentNameFactory
{
    public static String ISSUE_COMMENT_NAME_PREFIX = "comment-";

    public static String create( final Instant created )
    {
        return ISSUE_COMMENT_NAME_PREFIX + created.toEpochMilli();
    }

}
