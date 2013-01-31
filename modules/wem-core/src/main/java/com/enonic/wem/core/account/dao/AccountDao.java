package com.enonic.wem.core.account.dao;

import java.util.Collection;

import javax.jcr.Session;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;

public interface AccountDao
    extends AccountDaoConstants
{
    public boolean deleteAccount( AccountKey key, Session session )
        throws Exception;

    public boolean deleteUserStore( UserStoreName name, Session session )
        throws Exception;

    public void createUserStore( UserStore userStore, Session session )
        throws Exception;

    public void createUser( UserAccount user, Session session )
        throws Exception;

    public void createGroup( GroupAccount group, Session session )
        throws Exception;

    public void createRole( RoleAccount role, Session session )
        throws Exception;

    public void updateUser( UserAccount user, Session session )
        throws Exception;

    public void updateGroup( GroupAccount group, Session session )
        throws Exception;

    public void updateRole( RoleAccount role, Session session )
        throws Exception;

    public AccountKeys getMembers( AccountKey accountKey, Session session )
        throws Exception;

    public void setMembers( AccountKey nonUserAccount, AccountKeys members, Session session )
        throws Exception;

    public AccountKeys getUserStoreAdministrators( UserStoreName userStoreName, Session session )
        throws Exception;

    void setUserStoreAdministrators( UserStoreName userStoreName, AccountKeys administrators, Session session )
        throws Exception;

    public boolean accountExists( AccountKey accountKey, Session session )
        throws Exception;

    public UserAccount findUser( AccountKey accountKey, boolean includeProfile, boolean includePhoto, Session session )
        throws Exception;

    public GroupAccount findGroup( AccountKey accountKey, boolean includeMembers, Session session )
        throws Exception;

    public RoleAccount findRole( AccountKey accountKey, boolean includeMembers, Session session )
        throws Exception;

    public Account findAccount( AccountKey accountKey, Session session )
        throws Exception;

    public UserStoreNames getUserStoreNames( Session session )
        throws Exception;

    public UserStore getUserStore( UserStoreName userStoreName, boolean includeConfig, boolean includeStatistics, Session session )
        throws Exception;

    public void updateUserStore( UserStore userStore, Session session )
        throws Exception;

    public Collection<AccountKey> getAllAccountKeys( final Session session )
        throws Exception;

}
