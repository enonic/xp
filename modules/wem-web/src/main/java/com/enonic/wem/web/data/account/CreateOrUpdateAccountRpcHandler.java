package com.enonic.wem.web.data.account;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.account.editor.AccountEditors;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.account.selector.AccountKeySelector;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;
import com.enonic.wem.web.rest2.service.upload.UploadItem;
import com.enonic.wem.web.rest2.service.upload.UploadService;

@Component
public final class CreateOrUpdateAccountRpcHandler
    extends AbstractDataRpcHandler
{
    private UploadService uploadService;

    public CreateOrUpdateAccountRpcHandler()
    {
        super( "account_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Account account = getAccountFromRequest( context );
        if ( accountExists( account.getKey() ) )
        {
            updateAccount( account );
            context.setResult( CreateOrUpdateAccountJsonResult.updated() );
        }
        else
        {
            createAccount( account );
            context.setResult( CreateOrUpdateAccountJsonResult.created() );
        }
    }

    private boolean accountExists( final AccountKey accountKey )
    {
        final AccountResult accounts = this.client.execute( Commands.account().find().selector( AccountSelectors.keys( accountKey ) ) );
        return !accounts.isEmpty();
    }

    private void createAccount( final Account account )
        throws Exception
    {
        this.client.execute( Commands.account().create().account( account ) );
    }

    private void updateAccount( final Account account )
        throws Exception
    {
        final AccountKeySelector fromKey = AccountSelectors.keys( account.getKey() );
        final AccountEditor setAccount = AccountEditors.setAccount( account );
        this.client.execute( Commands.account().update().selector( fromKey ).editor( setAccount ) );
    }

    private Account getAccountFromRequest( final JsonRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();
        final AccountKey accountKey = AccountKey.from( key );

        final Account account;
        if ( accountKey.isUser() )
        {
            final UserAccount user = UserAccount.create( accountKey );
            user.setEmail( context.param( "email" ).required().asString() );
            if ( !context.param( "imageRef" ).isNull() )
            {
                final byte[] image = getImageContent( context.param( "imageRef" ).asString() );
                user.setImage( image );
            }
            account = user;
        }
        else
        {
            final NonUserAccount nonUserAccount;
            if ( accountKey.isGroup() )
            {
                nonUserAccount = GroupAccount.create( accountKey );
            }
            else
            {
                nonUserAccount = RoleAccount.create( accountKey );
            }
            final String[] members = context.param( "members" ).asStringArray();
            nonUserAccount.setMembers( AccountKeySet.from( members ) );
            account = nonUserAccount;
        }
        account.setDisplayName( context.param( "displayName" ).required().asString() );

        return account;
    }

    private byte[] getImageContent( final String imageId )
        throws IOException
    {
        final UploadItem item = uploadService.getItem( imageId );
        if ( item != null )
        {
            final File file = item.getFile();
            if ( file.exists() )
            {
                return FileUtils.readFileToByteArray( file );
            }
        }
        return null;
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}