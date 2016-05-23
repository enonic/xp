package com.enonic.xp.lib.auth;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.UserStore;

public final class GetUserStoreHandler
    implements ScriptBean
{
    private PortalRequest request;

    public UserStoreMapper execute()
    {
        final UserStore userStore = this.request.getUserStore();
        if ( userStore != null )
        {
            return new UserStoreMapper( userStore );
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
    }
}
