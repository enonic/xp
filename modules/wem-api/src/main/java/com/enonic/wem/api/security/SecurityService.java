package com.enonic.wem.api.security;

import java.util.List;
import java.util.Optional;

import com.enonic.wem.api.security.acl.UserStoreAccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;

public interface SecurityService
{
    UserStores getUserStores();

    UserStore getUserStore( UserStoreKey userStore );

    UserStoreAccessControlList getUserStorePermissions( UserStoreKey userStore );

    UserStore createUserStore( CreateUserStoreParams createUserStoreParams );

    UserStore updateUserStore( UpdateUserStoreParams updateUserStoreParams );

    Principals findPrincipals( UserStoreKey useStore, List<PrincipalType> types, String query );

    AuthenticationInfo authenticate( AuthenticationToken token );

    User setPassword( PrincipalKey key, String password );

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

    /**
     * Looks up a user by key and returns an {@code Optional} with the user instance.
     *
     * @param userKey principal key of the user to retrieve
     * @return an {@link Optional} with the user
     */
    Optional<User> getUser( PrincipalKey userKey );

    /**
     * Retrieve the list of principals (groups or roles) that have the specified principal as a member.
     * The list returned will contain only direct memberships, i.e. not including transitive dependencies (group of a group).
     *
     * @param principalKey principal key to obtain memberships of
     * @return a list of {@link PrincipalKeys} containing the list of groups and roles that the principal is a member of
     */
    PrincipalKeys getMemberships( PrincipalKey principalKey );

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

    /**
     * Looks up a group by key and returns an {@code Optional} with the group instance.
     *
     * @param groupKey principal key of the group to retrieve
     * @return an {@link Optional} with the group
     */
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

    /**
     * Looks up a role by key and returns an {@code Optional} with the role instance.
     *
     * @param roleKey principal key of the role to retrieve
     * @return an {@link Optional} with the role
     */
    Optional<Role> getRole( PrincipalKey roleKey );

    /**
     * Looks up a principal by key and returns an {@code Optional} with the principal instance.
     *
     * @param principalKey key of the principal to retrieve
     * @return an {@link Optional} with the principal
     */
    Optional<? extends Principal> getPrincipal( PrincipalKey principalKey );

    /**
     * Looks up a set of principals by key and returns a list of with the {@link Principals} found.
     * If one or more principal keys cannot be found, they will be omitted from the result.
     *
     * @param principalKeys keys of the principals to retrieve
     * @return a {@link Principals} object containing the list of principals found
     */
    Principals getPrincipals( PrincipalKeys principalKeys );

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
