package com.enonic.wem.core.account;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class FindMembersHandler
    extends CommandHandler<FindMembers>
{
    private AccountDao accountDao;

    public FindMembersHandler()
    {
        super( FindMembers.class );
    }

    @Override
    public void handle( final CommandContext context, final FindMembers command )
        throws Exception
    {
        final AccountKey accountKey = command.getKey();
        command.setResult( accountDao.getMembers( accountKey, context.getJcrSession() ) );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
