package com.enonic.xp.content;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.Permission;

@PublicApi
public final class ContentAccessException
    extends BaseException
{
    private final User user;

    private final ContentPath contentPath;

    private final Permission permission;

    public ContentAccessException( final NodeAccessException nodeAccessException )
    {
        this( nodeAccessException, nodeAccessException.getUser(), translateNodePathToContentPath( nodeAccessException.getNodePath() ),
              nodeAccessException.getPermission() );
    }

    public ContentAccessException( final User user, final ContentPath contentPath, final Permission permission )
    {
        this( null, user, contentPath, permission );
    }

    private ContentAccessException( final Throwable cause, final User user, final ContentPath contentPath, final Permission permission )
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

    private static ContentPath translateNodePathToContentPath( final NodePath nodePath )
    {
        final int beginIndex = nodePath.toString().indexOf( '/', 1 );
        if ( beginIndex == -1 )
        {
            return ContentPath.ROOT;
        }
        else
        {
            return ContentPath.from( nodePath.toString().substring( beginIndex ) );
        }
    }
}
