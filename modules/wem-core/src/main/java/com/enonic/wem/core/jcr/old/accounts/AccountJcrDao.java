package com.enonic.wem.core.jcr.old.accounts;

import java.util.Collection;
import java.util.List;

public interface AccountJcrDao
{
    int getGroupsCount();

    int getUsersCount();

    List<JcrAccount> findAll( int from, int count );

    List<JcrUser> findAllUsers( int from, int count );

    List<JcrGroup> findAllGroups( int from, int count );

    JcrAccount findAccountById( String accountId );

    JcrUser findUserById( String accountId );

    JcrGroup findGroupById( String accountId );

    JcrRole findRoleById( String accountId );

    byte[] findUserPhotoById( String accountId );


    void saveAccount( JcrAccount account );

    void deleteAccount( JcrAccount account );

    void deleteAccount( String accountId );


    JcrUserStore findUserStoreByName( String userStoreName );

    void createUserStore( JcrUserStore userStore );


    void addMemberships( String groupId, Collection<String> memberIds );

    void addMemberships( String groupId, String... memberId );
}