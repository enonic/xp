package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.lib.common.AuthDescriptorMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;

public class GetAuthDescriptorHandler
    implements ScriptBean
{
    private UserStoreKey userStoreKey;

    private Supplier<SecurityService> securityService;

    private Supplier<AuthDescriptorService> authDescriptorService;

    public void setUserStoreKey( final String userStoreKey )
    {
        this.userStoreKey = UserStoreKey.from( userStoreKey );
    }

    public AuthDescriptorMapper getAuthDescriptor()
    {
        final UserStore userStore = securityService.get().getUserStore( this.userStoreKey );
        final AuthConfig authConfig = userStore == null ? null : userStore.getAuthConfig();
        final ApplicationKey idProviderKey = authConfig == null ? null : authConfig.getApplicationKey();
        final AuthDescriptor authDescriptor = idProviderKey == null ? null : authDescriptorService.get().getDescriptor( idProviderKey );

        if ( authDescriptor != null )
        {
            return new AuthDescriptorMapper( authDescriptor );
        }

        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
        this.authDescriptorService = context.getService( AuthDescriptorService.class );
    }
}
