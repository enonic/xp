package com.enonic.wem.web.data.account;

import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountFacets;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;

public abstract class AbstractAccountRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    protected AccountResult createAccountResult( final int totalSize, final Account... accounts )
    {
        final List<Account> accountList = Lists.newArrayList( accounts );
        final AccountResult result = new AccountResult( totalSize, accountList );
        result.setFacets( new AccountFacets() );
        return result;
    }

    protected UserAccount createUser( final String qName )
    {
        final AccountKey accountKey = AccountKey.user( qName );
        return createUser( accountKey );
    }

    protected UserAccount createUser( final AccountKey accountKey )
    {
        final UserAccount user = UserAccount.create( accountKey );
        user.setDisplayName( accountKey.getLocalName().toUpperCase() );
        user.setEmail( accountKey.getLocalName() + "@" + accountKey.getUserStore() + ".com" );
        user.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        user.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        user.setImage( "image".getBytes() );
        return user;
    }

    protected GroupAccount createGroup( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = AccountKey.group( qName );
        final GroupAccount group = GroupAccount.create( accountKey );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeySet.from( members ) );
        return group;
    }

    protected RoleAccount createRole( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = AccountKey.role( qName );
        final RoleAccount group = RoleAccount.create( accountKey );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeySet.from( members ) );
        return group;
    }
}
