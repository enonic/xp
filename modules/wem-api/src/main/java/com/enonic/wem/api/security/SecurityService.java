package com.enonic.wem.api.security;

import java.util.List;
import java.util.Optional;

import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;

public interface SecurityService
{
    UserStores getUserStores();

    Principals findPrincipals( UserStoreKey useStore, List<PrincipalType> types, String query );

    AuthenticationInfo authenticate( AuthenticationToken token );

    void setPassword( PrincipalKey key, String password );

    /**
     * Creates a user on the specified user store.
     *
     * @param createUserParams details of the user to be created
     * @return the user created
     * @throws com.enonic.wem.api.security.PrincipalAlreadyExistsException if a user with the same key already exists
     */
    User createUser( CreateUserParams createUserParams );

    /**
     * Updates an existing user.
     *
     * @param updateUserParams details of the user to be updated
     * @return the user updated
     * @throws com.enonic.wem.api.security.PrincipalNotFoundException if the specified user does not exist
     */
    User updateUser( UpdateUserParams updateUserParams );

    Optional<User> getUser( PrincipalKey userKey );

    /**
     * Creates a group on the specified user store.
     *
     * @param createGroupParams details of the group to be created
     * @return the group created
     * @throws com.enonic.wem.api.security.PrincipalAlreadyExistsException if a group with the same key already exists
     */
    Group createGroup( CreateGroupParams createGroupParams );

    /**
     * Updates an existing group.
     *
     * @param updateGroupParams details of the group to be updated
     * @return the group updated
     * @throws com.enonic.wem.api.security.PrincipalNotFoundException if the specified group does not exist
     */
    Group updateGroup( UpdateGroupParams updateGroupParams );

    Optional<Group> getGroup( PrincipalKey groupKey );

    /**
     * Creates a role on the specified user store.
     *
     * @param createRoleParams details of the role to be created
     * @return the role created
     * @throws com.enonic.wem.api.security.PrincipalAlreadyExistsException if a role with the same key already exists
     */
    Role createRole( CreateRoleParams createRoleParams );

    /**
     * Updates an existing role.
     *
     * @param updateRoleParams details of the role to be updated
     * @return the role updated
     * @throws com.enonic.wem.api.security.PrincipalNotFoundException if the specified role does not exist
     */
    Role updateRole( UpdateRoleParams updateRoleParams );

    Optional<Role> getRole( PrincipalKey roleKey );

    Optional<? extends Principal> getPrincipal( PrincipalKey principalKey );

    /**
     * Deletes an existing principal.
     *
     * @param principalKey key of the principal to be deleted
     * @throws com.enonic.wem.api.security.PrincipalNotFoundException if the specified principal does not exist
     */
    void deletePrincipal( PrincipalKey principalKey );

    PrincipalQueryResult query( PrincipalQuery query );

    PrincipalRelationships getRelationships( PrincipalKey from );

    void addRelationship( PrincipalRelationship relationship );

    void removeRelationship( PrincipalRelationship relationship );

    void removeRelationships( PrincipalKey from );

}
