package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.lib.common.UserStoreMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;

public final class GetUserStoreHandler
    implements ScriptBean
{
    private UserStoreKey userStoreKey;

    private Supplier<SecurityService> securityService;

    public void setUserStoreKey( final String userStoreKey )
    {
        this.userStoreKey = UserStoreKey.from( userStoreKey );
    }

    public UserStoreMapper getUserStore()
    {
        final UserStore userStore = securityService.get().getUserStore( userStoreKey );

        if ( userStore != null )
        {
            return new UserStoreMapper( userStore, true );
        }

        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        securityService = context.getService( SecurityService.class );
    }
}
