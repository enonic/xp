package com.enonic.wem.web.rest.account;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;

public abstract class ModelTranslator<T, E>
{
    public abstract T toModel( final E entity );

    public abstract T toInfoModel( final E entity );

    public boolean isAuthenticatedUsersRole( final GroupEntity entity )
    {
        return GroupType.AUTHENTICATED_USERS.equals( entity.getType() );
    }

    public boolean isAnonymousUsersRole( final GroupEntity entity )
    {
        return GroupType.ANONYMOUS.equals( entity.getType() );
    }

}
