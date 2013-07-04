package com.enonic.wem.admin.rpc.account;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.RoleKey;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.account.query.AccountFacets;
import com.enonic.wem.api.account.query.AccountQueryHits;

public abstract class AbstractAccountRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private final static DateTime DATE_TIME = new DateTime( 2012, 1, 1, 10, 01, 10, 101, DateTimeZone.UTC );

    protected AccountQueryHits createAccountResult( final int totalSize, final Accounts accounts )
    {
        final AccountQueryHits result = new AccountQueryHits( totalSize, accounts.getKeys() );
        result.setFacets( new AccountFacets() );
        return result;
    }

    protected Accounts createAccountsObject( final Account... accounts )
    {
        return Accounts.from( accounts );
    }

    protected UserAccount createUser( final String qName )
    {
        final AccountKey accountKey = UserKey.from( qName );
        return createUser( accountKey );
    }

    protected UserAccount createUser( final AccountKey accountKey )
    {
        final UserAccount user = UserAccount.create( accountKey.asUser() );
        user.setDisplayName( accountKey.getLocalName().toUpperCase() );
        user.setEmail( accountKey.getLocalName() + "@" + accountKey.getUserStore() + ".com" );
        user.setCreatedTime( DATE_TIME );
        user.setModifiedTime( DATE_TIME );
        user.setImage( "image".getBytes() );
        return user;
    }

    protected GroupAccount createGroup( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = GroupKey.from( qName );
        final GroupAccount group = GroupAccount.create( accountKey.asGroup() );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DATE_TIME );
        group.setModifiedTime( DATE_TIME );
        group.setMembers( AccountKeys.from( members ) );
        return group;
    }

    protected RoleAccount createRole( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = RoleKey.from( qName );
        final RoleAccount group = RoleAccount.create( accountKey.asRole() );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DATE_TIME );
        group.setModifiedTime( DATE_TIME );
        group.setMembers( AccountKeys.from( members ) );
        return group;
    }
}
