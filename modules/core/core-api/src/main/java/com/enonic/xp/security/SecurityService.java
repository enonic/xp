package com.enonic.xp.security;

import java.util.Optional;

import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.Clients;
import com.enonic.xp.security.token.CreateClientParams;
import com.enonic.xp.security.token.CreateScopeParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.UpdateClientParams;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.security.token.UpdateScopeParams;


public interface SecurityService
{
    IdProviders getIdProviders();

    IdProvider getIdProvider( IdProviderKey idProviderKey );

    IdProviderAccessControlList getIdProviderPermissions( IdProviderKey idProviderKey );

    IdProvider createIdProvider( CreateIdProviderParams createIdProviderParams );

    IdProvider updateIdProvider( UpdateIdProviderParams updateIdProviderParams );

    AuthenticationInfo authenticate( AuthenticationToken token );

    User setPassword( PrincipalKey key, String password );

    /**
     * Creates a user on the specified id provider.
     *
     * @param createUserParams details of the user to be created
     * @return the user created
     * @throws PrincipalAlreadyExistsException if a user with the same key already exists
     */
    User createUser( CreateUserParams createUserParams );

    /**
     * Updates an existing user.
     *
     * @param updateUserParams details of the user to be updated
     * @return the user updated
     * @throws PrincipalNotFoundException if the specified user does not exist
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
     * Retrieve the list of principals (groups or roles) that have the specified principal as a member, directly or indirectly.
     * The list returned will contain all memberships, including transitive dependencies (group of a group).
     *
     * @param principalKey principal key to obtain memberships of
     * @return a list of {@link PrincipalKeys} containing the list of groups and roles that the principal is a member of
     */
    PrincipalKeys getAllMemberships( PrincipalKey principalKey );

    /**
     * Creates a group on the specified id provider.
     *
     * @param createGroupParams details of the group to be created
     * @return the group created
     * @throws PrincipalAlreadyExistsException if a group with the same key already exists
     */
    Group createGroup( CreateGroupParams createGroupParams );

    /**
     * Updates an existing group.
     *
     * @param updateGroupParams details of the group to be updated
     * @return the group updated
     * @throws PrincipalNotFoundException if the specified group does not exist
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
     * Creates a role on the specified id provider.
     *
     * @param createRoleParams details of the role to be created
     * @return the role created
     * @throws PrincipalAlreadyExistsException if a role with the same key already exists
     */
    Role createRole( CreateRoleParams createRoleParams );

    /**
     * Updates an existing role.
     *
     * @param updateRoleParams details of the role to be updated
     * @return the role updated
     * @throws PrincipalNotFoundException if the specified role does not exist
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
     * @throws PrincipalNotFoundException if the specified principal does not exist
     */
    void deletePrincipal( PrincipalKey principalKey );

    /**
     * Deletes an existing idProvider.
     *
     * @param idProviderKey key of the idProvider to be deleted
     * @throws IdProviderNotFoundException if the specified idProvider does not exist
     */
    void deleteIdProvider( IdProviderKey idProviderKey );

    PrincipalQueryResult query( PrincipalQuery query );

    UserQueryResult query( UserQuery query );

    PrincipalRelationships getRelationships( PrincipalKey from );

    void addRelationship( PrincipalRelationship relationship );

    void removeRelationship( PrincipalRelationship relationship );

    /**
     * Adds a scope to an id provider: what its clients may ask for and what a user may approve.
     * Deleting the id provider deletes its scopes. Reading needs no permission, since the scopes
     * are what every client is told; writing needs the permission to administer the id provider.
     *
     * @throws com.enonic.xp.security.token.ScopeAlreadyExistsException when the id provider has a scope with that id
     * @throws IdProviderNotFoundException                              when there is no such id provider
     */
    Scope createScope( CreateScopeParams params );

    /**
     * Changes a scope's title, description, roles or required flag. Empty when the id provider has
     * no such scope.
     */
    Optional<Scope> updateScope( UpdateScopeParams params );

    /**
     * The id provider's scope with the given id.
     */
    Optional<Scope> getScope( IdProviderKey idProvider, String id );

    /**
     * The id provider's scopes, by id. Empty when it has none, or does not exist.
     */
    Scopes getScopes( IdProviderKey idProvider );

    /**
     * Removes a scope; false when the id provider has no such scope. Grants already holding the
     * scope keep it, with the restriction fixed when they were approved.
     */
    boolean deleteScope( IdProviderKey idProvider, String id );

    /**
     * Registers a client with an id provider: who it is to the user, which flows it may use, and
     * the most it may ask for. Deleting the id provider deletes its clients. Reading needs no
     * permission, since a client's record is what the consent page shows and what the client
     * itself is told; writing needs the permission to administer the id provider.
     *
     * @throws com.enonic.xp.security.token.ClientAlreadyExistsException when the id provider has a client with that id
     * @throws IdProviderNotFoundException                               when there is no such id provider
     */
    Client createClient( CreateClientParams params );

    /**
     * Changes a client. Empty when the id provider has no such client.
     */
    Optional<Client> updateClient( UpdateClientParams params );

    /**
     * The id provider's client with the given id.
     */
    Optional<Client> getClient( IdProviderKey idProvider, String id );

    /**
     * The id provider's clients, by id. Empty when it has none, or does not exist.
     */
    Clients getClients( IdProviderKey idProvider );

    /**
     * Removes a client; false when the id provider has no such client. Grants to the client stay
     * and keep refreshing, as a client without a record may: to stop them, revoke them, or keep the
     * client disabled instead of removing it.
     *
     * @throws IllegalArgumentException for a client XP registers itself, which can be disabled but not removed
     */
    boolean deleteClient( IdProviderKey idProvider, String id );
}
