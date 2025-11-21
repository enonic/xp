package com.enonic.xp.content;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.Permission;

@PublicApi
public final class ContentAccessException
    extends BaseException
{
    private final User user;

    private final ContentPath contentPath;

    private final Permission permission;

    public ContentAccessException( final Throwable cause, final User user, final ContentPath contentPath, final Permission permission )
    {
        super( cause, MessageFormat.format( "Access denied to [{0}] for [{1}] by user [{2}] {3}", contentPath, permission,
                                     user == null ? "unknown" : user.getKey(),
                                     user != null && user.getDisplayName() != null ? "''" + user.getDisplayName() + "''" : "" ) );
        this.user = user;
        this.contentPath = contentPath;
        this.permission = permission;
    }

    public User getUser()
    {
        return user;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public Permission getPermission()
    {
        return permission;
    }
}
