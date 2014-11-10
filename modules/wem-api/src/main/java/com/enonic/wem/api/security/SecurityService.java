package com.enonic.wem.api.security;

import java.util.Optional;

import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;

public interface SecurityService
{
    UserStores getUserStores();

    Principals getPrincipals( UserStoreKey useStore, PrincipalType type );

    AuthenticationInfo authenticate( AuthenticationToken token );

    void setPassword( PrincipalKey key, String password );

    User createUser( CreateUserParams createUser );

    User updateUser( UpdateUserParams updateUser );

    Optional<User> getUser( PrincipalKey userKey );

    Group createGroup( CreateGroupParams group );

    Group updateGroup( UpdateGroupParams group );

    Optional<Group> getGroup( PrincipalKey groupKey );

    Role createRole( CreateRoleParams createRole );

    Role updateRole( UpdateRoleParams updateRole );

    Optional<Role> getRole( PrincipalKey roleKey );

    Optional<? extends Principal> getPrincipal( PrincipalKey groupKey );

    PrincipalQueryResult query( PrincipalQuery query );

    PrincipalRelationships getRelationships( PrincipalKey from );

    void addRelationship( PrincipalRelationship relationship );

    void removeRelationship( PrincipalRelationship relationship );

    void removeRelationships( PrincipalKey from );

}
