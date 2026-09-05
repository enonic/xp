package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.acl.Permission;

import static java.util.Objects.requireNonNull;

public final class GetCurrentContentHandler
    implements ScriptBean
{
    private PortalRequest request;

    public ContentMapper execute()
    {
        final Content content = this.request.getContent();
        return content != null && isReadable( content ) ? new ContentMapper( content ) : null;
    }

    private static boolean isReadable( final Content content )
    {
        return content.getPermissions().isAllowedFor( ContextAccessor.current().getAuthInfo().getPrincipals(), Permission.READ );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = requireNonNull( context.getBinding( PortalRequest.class ).get(), "no request bound" );
    }
}
