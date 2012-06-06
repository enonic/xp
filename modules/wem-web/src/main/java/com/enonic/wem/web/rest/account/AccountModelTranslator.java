package com.enonic.wem.web.rest.account;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.support.EntityPageList;

@Component
public final class AccountModelTranslator
        extends ModelTranslator<AccountModel, Object>
{
    @Autowired
    private UserModelTranslator userModelTranslator;

    @Autowired
    private GroupModelTranslator groupModelTranslator;


    public AccountModel toModel( final Object entity )
    {
        AccountModel model = null;
        if ( entity instanceof UserEntity )
        {
            model = userModelTranslator.toModel( (UserEntity) entity );
        }
        else if ( entity instanceof GroupEntity )
        {
            model = groupModelTranslator.toModel( (GroupEntity) entity );
        }
        else
        {
            throw new IllegalArgumentException( "Expected UserEntity or GroupEntity." );
        }
        return model;
    }

    public AccountModel toInfoModel( final Object entity )
    {
        AccountModel model;
        if ( entity instanceof UserEntity )
        {
            model = userModelTranslator.toInfoModel( (UserEntity) entity );
        }
        else if ( entity instanceof GroupEntity )
        {
            model = groupModelTranslator.toInfoModel( (GroupEntity) entity );
        }
        else
        {
            throw new IllegalArgumentException( "Expected UserEntity or GroupEntity." );
        }
        return model;
    }


    public AccountsModel toModel( final Collection accounts )
    {
        final AccountsModel model = new AccountsModel();
        model.setTotal( accounts.size() );

        for ( final Object entity : accounts )
        {
            AccountModel aModel = toModel( entity );
            model.addAccount( aModel );
        }
        return model;
    }

    public AccountsModel toModel( final EntityPageList accounts )
    {
        final AccountsModel accountsModel = toModel( accounts.getList() );
        accountsModel.setTotal( accounts.getTotal() );
        return accountsModel;
    }

    public AccountsModel toInfoModel( final Collection accounts )
    {
        final AccountsModel model = new AccountsModel();
        model.setTotal( accounts.size() );
        for ( final Object entity : accounts )
        {
            model.addAccount( toInfoModel( entity ) );
        }
        return model;
    }

    public AccountsModel toInfoModel( final EntityPageList accounts )
    {
        final AccountsModel accountsModel = toInfoModel( accounts.getList() );
        accountsModel.setTotal( accounts.getTotal() );
        return accountsModel;
    }


    public UserModelTranslator getUserModelTranslator()
    {
        return userModelTranslator;
    }

    public GroupModelTranslator getGroupModelTranslator()
    {
        return groupModelTranslator;
    }
}
