package com.enonic.wem.api.account.builder;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

import static org.junit.Assert.*;

public class AccountBuildersTest
{
    private byte[] photo;

    private AccountKeySet members;

    @Before
    public void setUp()
    {
        this.photo = new byte[10];
        this.members = AccountKeySet.from( "role:other:admin" );
    }

    private UserAccount buildUser()
    {
        final UserAccountBuilder builder = AccountBuilders.user( "other:dummy" );

        assertSame( builder, builder.email( "dummy@other.com" ) );
        assertSame( builder, builder.displayName( "Dummy User" ) );
        assertSame( builder, builder.photo( this.photo ) );

        return builder.build();
    }

    private void assertUser( final UserAccount account )
    {
        assertNotNull( account );
        assertEquals( AccountKey.user( "other:dummy" ), account.getKey() );
        assertAccount( account );

        assertNull( account.getLastLoginTime() );
        assertEquals( "Dummy User", account.getDisplayName() );
        assertEquals( "dummy@other.com", account.getEmail() );
        assertSame( this.photo, account.getPhoto() );
    }

    private GroupAccount buildGroup()
    {
        final GroupAccountBuilder builder = AccountBuilders.group( "other:dummy" );

        assertSame( builder, builder.displayName( "Dummy Group" ) );
        assertSame( builder, builder.members( this.members ) );

        return builder.build();
    }

    private RoleAccount buildRole()
    {
        final RoleAccountBuilder builder = AccountBuilders.role( "other:dummy" );

        assertSame( builder, builder.displayName( "Dummy Role" ) );
        assertSame( builder, builder.members( this.members ) );

        return builder.build();
    }

    private void assertGroup( final GroupAccount account )
    {
        assertNotNull( account );
        assertEquals( AccountKey.group( "other:dummy" ), account.getKey() );
        assertAccount( account );

        assertEquals( "Dummy Group", account.getDisplayName() );
        assertSame( this.members, account.getMembers() );
    }

    private void assertRole( final RoleAccount account )
    {
        assertNotNull( account );
        assertEquals( AccountKey.role( "other:dummy" ), account.getKey() );
        assertAccount( account );

        assertEquals( "Dummy Role", account.getDisplayName() );
        assertSame( this.members, account.getMembers() );
    }

    @Test
    public void testUser()
    {
        final UserAccount account1 = buildUser();
        assertUser( account1 );

        final UserAccountBuilder builder = AccountBuilders.from( account1 );
        final UserAccount account2 = builder.build();
        assertUser( account2 );
    }

    @Test
    public void testGroup()
    {
        final GroupAccount account1 = buildGroup();
        assertGroup( account1 );

        final GroupAccountBuilder builder = AccountBuilders.from( account1 );
        final GroupAccount account2 = builder.build();
        assertGroup( account2 );
    }

    @Test
    public void testRole()
    {
        final RoleAccount account1 = buildRole();
        assertRole( account1 );

        final RoleAccountBuilder builder = AccountBuilders.from( account1 );
        final RoleAccount account2 = builder.build();
        assertRole( account2 );
    }

    protected void assertAccount( final Account account )
    {
        assertNull( account.getCreatedTime() );
        assertNull( account.getModifiedTime() );
        assertFalse( account.isEditable() );
        assertFalse( account.isDeleted() );
    }
}
