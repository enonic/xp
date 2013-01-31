package com.enonic.wem.core.search;

import java.util.Collection;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

@Component
public class ReindexService
{

    private IndexService indexService;

    private JcrSessionProvider jcrSessionProvider;

    private AccountDao accountDao;


    public void reindexAccounts()
        throws Exception
    {
        Session session = jcrSessionProvider.login();

        final Collection<AccountKey> accountKeys = accountDao.getAllAccountKeys( session );

        for ( AccountKey accountKey : accountKeys )
        {
            final Account account = accountDao.findAccount( accountKey, session );

            System.out.println( "indexing account: " + account.getDisplayName() );

            indexService.index( account );
        }
    }


    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Autowired
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
    }

    @Autowired
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
