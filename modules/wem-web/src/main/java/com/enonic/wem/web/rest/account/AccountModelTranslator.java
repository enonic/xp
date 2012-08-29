package com.enonic.wem.web.rest.account;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;

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

    public UserModelTranslator getUserModelTranslator()
    {
        return userModelTranslator;
    }

    public GroupModelTranslator getGroupModelTranslator()
    {
        return groupModelTranslator;
    }
}
