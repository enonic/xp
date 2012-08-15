package com.enonic.wem.core.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.selector.AccountKeySelector;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class DeleteAccountsHandler
    extends CommandHandler<DeleteAccounts>
{
    private GroupDao groupDao;

    private UserDao userDao;

    private UserStoreDao userStoreDao;

    public DeleteAccountsHandler()
    {
        super( DeleteAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteAccounts command )
        throws Exception
    {
        final AccountKeySet accountKeys = evaluateSelector( command.getSelector() );

        int accountsDeleted = 0;
        for ( AccountKey accountKey : accountKeys )
        {
            if ( deleteAccount( accountKey ) )
            {
                accountsDeleted++;
            }
        }

        command.setResult( accountsDeleted );
    }

    private boolean deleteAccount( final AccountKey account )
    {
        return account.isUser() ? deleteUser( account ) : deleteGroup( account );
    }

    private boolean deleteUser( final AccountKey userAccount )
    {
        final UserStoreEntity userStore = userStoreDao.findByName( userAccount.getUserStore() );
        if ( userStore == null )
        {
            return false;
        }

        final QualifiedUsername qualifiedName = new QualifiedUsername( userStore.getName(), userAccount.getLocalName() );
        final UserEntity userEntity = userDao.findByQualifiedUsername( qualifiedName );
        if ( userEntity == null )
        {
            return false;
        }
        userDao.delete( userEntity );
        return true;
    }

    private boolean deleteGroup( final AccountKey groupAccount )
    {
        final UserStoreEntity userStore = userStoreDao.findByName( groupAccount.getUserStore() );
        if ( userStore == null )
        {
            return false;
        }

        final List<GroupEntity> groups = groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), groupAccount.getLocalName(), false );
        if ( ( groups == null ) || groups.isEmpty() )
        {
            return false;
        }
        groupDao.delete( groups.get( 0 ) );
        return true;
    }

    private AccountKeySet evaluateSelector( final AccountSelector accountSelector )
    {
        if ( accountSelector instanceof AccountKeySelector )
        {
            final AccountKeySelector accountKeySelector = (AccountKeySelector) accountSelector;
            return accountKeySelector.getKeys();
        }
        else
        {
            throw new SystemException( "Account selector of type {0} is not supported", accountSelector.getClass().getName() );
        }
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }
}
