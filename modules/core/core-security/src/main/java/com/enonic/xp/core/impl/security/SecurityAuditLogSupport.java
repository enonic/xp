package com.enonic.xp.core.impl.security;

import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateServiceAccountParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;

public interface SecurityAuditLogSupport
{
    void createUser( CreateUserParams params );

    void updateUser( UpdateUserParams params );

    void createGroup( CreateGroupParams params );

    void updateGroup( UpdateGroupParams params );

    void createRole( CreateRoleParams params );

    void updateRole( UpdateRoleParams params );

    void createIdProvider( CreateIdProviderParams params );

    void updateIdProvider( UpdateIdProviderParams params );

    void removeIdProvider( IdProviderKey key );

    void removePrincipal( PrincipalKey key );

    void addRelationship( PrincipalRelationship relationship );

    void removeRelationship( PrincipalRelationship relationship );

    void removeRelationships( PrincipalKey key );

    void setPassword( PrincipalKey key );

    void createServiceAccount( CreateServiceAccountParams params );
}
