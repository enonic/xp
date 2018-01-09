package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.common.PermissionMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.UserStoreAccess;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

public final class GetUserstorePermissionsHandler
    implements ScriptBean
{
    private UserStoreKey userStoreKey;

    private Supplier<SecurityService> securityService;

    public void setUserStoreKey( final String userStoreKey )
    {
        this.userStoreKey = UserStoreKey.from( userStoreKey );
    }

    public List<PermissionMapper> getUserstorePermissions()
    {
        final UserStore userStore = securityService.get().getUserStore( userStoreKey );

        if ( userStore != null )
        {
            final UserStoreAccessControlList userStorePermissions = securityService.get().getUserStorePermissions( userStoreKey );
            final Principals principals = securityService.get().getPrincipals( userStorePermissions.getAllPrincipals() );
            return principals.stream().
                map( ( principal ) -> new PermissionMapper( principal, getAccess( principal, userStorePermissions ) ) ).
                collect( Collectors.toList() );
        }

        return null;
    }

    private UserStoreAccess getAccess( final Principal principal, final UserStoreAccessControlList userStoreAccessControlList )
    {
        for ( UserStoreAccessControlEntry entry : userStoreAccessControlList )
        {
            if ( entry.getPrincipal().equals( principal.getKey() ) )
            {
                return entry.getAccess();
            }
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        securityService = context.getService( SecurityService.class );
    }
}
