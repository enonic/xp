package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

public abstract class AccountBuilders
{
    public static UserAccountBuilder user( final String qName )
    {
        final UserAccountImpl account = new UserAccountImpl();
        account.key = AccountKey.user( qName );
        return account;
    }

    public static GroupAccountBuilder group( final String qName )
    {
        final GroupAccountImpl account = new GroupAccountImpl();
        account.key = AccountKey.group( qName );
        return account;
    }

    public static RoleAccountBuilder role( final String qName )
    {
        final RoleAccountImpl account = new RoleAccountImpl();
        account.key = AccountKey.role( qName );
        return account;
    }

    public static UserAccountBuilder from( final UserAccount account )
    {
        final UserAccountImpl result = new UserAccountImpl();
        result.key = account.getKey();
        result.displayName = account.getDisplayName();
        result.email = account.getEmail();
        result.photo = account.getPhoto();
        return result;
    }

    public static GroupAccountBuilder from( final GroupAccount account )
    {
        final GroupAccountImpl result = new GroupAccountImpl();
        result.key = account.getKey();
        result.displayName = account.getDisplayName();
        result.members = account.getMembers();
        return result;
    }

    public static RoleAccountBuilder from( final RoleAccount account )
    {
        final RoleAccountImpl result = new RoleAccountImpl();
        result.key = account.getKey();
        result.displayName = account.getDisplayName();
        result.members = account.getMembers();
        return result;
    }
}
