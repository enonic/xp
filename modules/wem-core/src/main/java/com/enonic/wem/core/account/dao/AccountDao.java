package com.enonic.wem.core.account.dao;

import javax.jcr.Session;

import com.enonic.wem.api.account.AccountKey;

public interface AccountDao
    extends AccountDaoConstants
{
    public boolean delete( Session session, AccountKey key )
        throws Exception;
}
