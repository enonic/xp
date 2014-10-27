package com.enonic.wem.api.security;

import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;

public interface SecurityService
{
    UserStores getUserStores();

    Principals getPrincipals( UserStoreKey useStore, PrincipalType type );

    AuthenticationInfo authenticate( AuthenticationToken token );

    void setPassword( PrincipalKey key, String password );

    void createUser( User user );

    void updateUser( User user );

    User getUser( PrincipalKey userKey );

    void createGroup( Group group );

    void updateGroup( Group group );

    Group getGroup( PrincipalKey groupKey );

    PrincipalQueryResult query( PrincipalQuery query );

    PrincipalRelationships getRelationships( PrincipalKey from );

    void addRelationship( PrincipalRelationship relationship );

    void removeRelationship( PrincipalRelationship relationship );

    void removeRelationships( PrincipalKey from );

}
