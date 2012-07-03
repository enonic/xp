package com.enonic.wem.core.jcr.accounts;

import java.util.Collection;

import com.enonic.wem.core.jcr.PageList;

public interface AccountJcrDao
{
    JcrUser findUserById( String accountId );

    JcrGroup findGroupById( String accountId );

    PageList<JcrAccount> findAll( int index, int count );

    byte[] findUserPhotoByKey( String accountId );

    void saveAccount( JcrAccount account );

    void deleteAccount( JcrAccount account );

    void deleteAccount( String accountId );

    JcrUserStore findUserStoreByName( String userStoreName );

    void createUserStore( JcrUserStore userStore );

    void addMemberships( String groupId, Collection<String> memberIds );

    void addMembership( String groupId, String memberId );
}