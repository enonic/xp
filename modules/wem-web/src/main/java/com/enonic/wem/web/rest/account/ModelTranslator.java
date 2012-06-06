package com.enonic.wem.web.rest.account;


import java.text.SimpleDateFormat;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

public abstract class ModelTranslator<T, E>
{

    protected SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy-MM-dd" );


    public abstract T toModel( final E entity );

    public abstract T toInfoModel( final E entity );


    public boolean isEnterpriseAdminUser( final UserEntity entity )
    {
        if ( entity.isRoot() )
        {
            return true;
        }
        else if ( entity.isAnonymous() || ( entity.getUserGroup() == null ) )
        {
            return false;
        }
        else
        {
            return GroupType.ENTERPRISE_ADMINS.equals( entity.getUserGroup().getType() );
        }
    }

    public boolean isEnterpriseAdminsRole( final GroupEntity entity )
    {
        return GroupType.ENTERPRISE_ADMINS.equals( entity.getType() );
    }

    public boolean isAuthenticatedUsersRole( final GroupEntity entity )
    {
        return GroupType.AUTHENTICATED_USERS.equals( entity.getType() );
    }

    public boolean isAnonymousUsersRole( final GroupEntity entity )
    {
        return GroupType.ANONYMOUS.equals( entity.getType() );
    }

}
