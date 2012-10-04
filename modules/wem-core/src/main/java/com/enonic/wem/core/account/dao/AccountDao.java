package com.enonic.wem.core.account.dao;

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
    public boolean delete( Session session, AccountKey key )
        throws Exception;

    public void createUserStore( Session session, UserStore userStore )
        throws Exception;

    public void createUser( Session session, UserAccount user )
        throws Exception;

    public void createGroup( Session session, GroupAccount group )
        throws Exception;

    public void createRole( Session session, RoleAccount role )
        throws Exception;

    public void updateUser( Session session, UserAccount user )
        throws Exception;

    public void updateGroup( Session session, GroupAccount group )
        throws Exception;

    public void updateRole( Session session, RoleAccount role )
        throws Exception;

    public AccountKeys getMembers( Session session, AccountKey accountKey )
        throws Exception;

    public void setMembers( Session session, AccountKey nonUserAccount, AccountKeys members )
        throws Exception;

    public AccountKeys getUserStoreAdministrators( Session session, UserStoreName userStoreName )
        throws Exception;

    void setUserStoreAdministrators( Session session, UserStoreName userStoreName, AccountKeys administrators )
        throws Exception;

    public boolean accountExists( Session session, AccountKey accountKey )
        throws Exception;

    public UserAccount findUser( Session session, AccountKey accountKey, boolean includeProfile, boolean includePhoto )
        throws Exception;

    public GroupAccount findGroup( Session session, AccountKey accountKey, boolean includeMembers )
        throws Exception;

    public RoleAccount findRole( Session session, AccountKey accountKey, boolean includeMembers )
        throws Exception;

    public Account findAccount( Session session, AccountKey accountKey )
        throws Exception;

    public UserStoreNames getUserStoreNames( Session session )
        throws Exception;

    public UserStore getUserStore( Session session, UserStoreName userStoreName, boolean includeConfig, boolean includeStatistics )
        throws Exception;
}
