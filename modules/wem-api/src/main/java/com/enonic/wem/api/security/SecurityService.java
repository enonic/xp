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

    /**
     * Creates a user on the specified user store.
     *
     * @param createUserParams details of the user to be created
     * @return user created
     * @throws com.enonic.wem.api.security.PrincipalAlreadyExistsException if a user with the same key already exists
     */
    User createUser( CreateUserParams createUserParams );

    User updateUser( UpdateUserParams updateUser );

    Optional<User> getUser( PrincipalKey userKey );

    /**
     * Creates a group on the specified user store.
     *
     * @param createGroupParams details of the group to be created
     * @return group created
     * @throws com.enonic.wem.api.security.PrincipalAlreadyExistsException if a group with the same key already exists
     */
    Group createGroup( CreateGroupParams createGroupParams );

    Group updateGroup( UpdateGroupParams group );

    Optional<Group> getGroup( PrincipalKey groupKey );

    /**
     * Creates a role on the specified user store.
     *
     * @param createRoleParams details of the role to be created
     * @return role created
     * @throws com.enonic.wem.api.security.PrincipalAlreadyExistsException if a role with the same key already exists
     */
    Role createRole( CreateRoleParams createRoleParams );

    Role updateRole( UpdateRoleParams updateRole );

    Optional<Role> getRole( PrincipalKey roleKey );

    Optional<? extends Principal> getPrincipal( PrincipalKey principalKey );

    PrincipalQueryResult query( PrincipalQuery query );

    PrincipalRelationships getRelationships( PrincipalKey from );

    void addRelationship( PrincipalRelationship relationship );

    void removeRelationship( PrincipalRelationship relationship );

    void removeRelationships( PrincipalKey from );

}
