package com.enonic.wem.core.account;

import javax.inject.Inject;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.DeleteAccount;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.IndexService;

@Component
public class DeleteAccountHandler
    extends CommandHandler<DeleteAccount>
{
    private AccountDao accountDao;

    private IndexService indexService;

    public DeleteAccountHandler()
    {
        super( DeleteAccount.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteAccount command )
        throws Exception
    {
        final AccountKey accountKey = command.getKey();
        final Session session = context.getJcrSession();

        final boolean accountDeleted = this.accountDao.deleteAccount( accountKey, context.getJcrSession() );
        if ( accountDeleted )
        {
            this.indexService.deleteAccount( accountKey );
        }
        session.save();

        command.setResult( accountDeleted );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
