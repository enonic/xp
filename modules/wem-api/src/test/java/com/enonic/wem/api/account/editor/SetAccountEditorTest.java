package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

public class SetAccountEditorTest
{
    private UserAccount user;

    private GroupAccount group;

    private RoleAccount role;

    private UserAccount editableUser;

    private GroupAccount editableGroup;

    private RoleAccount editableRole;

    private byte[] photo;

    private AccountKeySet members;

    @Before
    public void setUp()
    {
        this.user = UserAccount.create( "other:dummy" );
        this.group = GroupAccount.create( "other:dummy" );
        this.role = RoleAccount.create( "other:dummy" );
        this.editableUser = UserAccount.create( "other:dummy" );
        this.editableGroup = GroupAccount.create( "other:dummy" );
        this.editableRole = RoleAccount.create( "other:dummy" );

        this.photo = new byte[10];
        this.members = AccountKeySet.from( "role:other:admin" );

        this.user.setEmail( "dummy@other.com" );
        this.user.setDisplayName( "Dummy User" );
        this.user.setImage( this.photo );

        this.group.setDisplayName( "Dummy Group" );
        this.group.setMembers( this.members );

        this.role.setDisplayName( "Dummy Role" );
        this.role.setMembers( this.members );
    }

    @Test
    public void testSetUser()
        throws Exception
    {
        assertEquals( false, this.editableUser.isDirty() );

        final SetAccountEditor editor = new SetAccountEditor( this.user );
        editor.edit( this.editableUser );

        assertEquals( true, this.editableUser.isDirty() );
        assertEquals( "dummy@other.com", this.editableUser.getEmail() );
        assertEquals( "Dummy User", this.editableUser.getDisplayName() );
        assertSame( this.photo, this.editableUser.getImage() );
    }

    @Test
    public void testSetGroup()
        throws Exception
    {
        assertEquals( false, this.editableGroup.isDirty() );

        final SetAccountEditor editor = new SetAccountEditor( this.group );
        editor.edit( this.editableGroup );

        assertEquals( true, this.editableGroup.isDirty() );
        assertEquals( "Dummy Group", this.editableGroup.getDisplayName() );
        assertSame( this.members, this.editableGroup.getMembers() );
    }

    @Test
    public void testSetRole()
        throws Exception
    {
        assertEquals( false, this.editableRole.isDirty() );

        final SetAccountEditor editor = new SetAccountEditor( this.role );
        editor.edit( this.editableRole );

        assertEquals( true, this.editableRole.isDirty() );
        assertEquals( "Dummy Role", this.editableRole.getDisplayName() );
        assertSame( this.members, this.editableRole.getMembers() );
    }

    @Test
    public void testSetUser_Wrong()
        throws Exception
    {
        assertEquals( false, this.editableUser.isDirty() );

        final SetAccountEditor editor = new SetAccountEditor( this.group );
        editor.edit( this.editableUser );

        assertEquals( false, this.editableUser.isDirty() );
    }

    @Test
    public void testSetGroup_Wrong()
        throws Exception
    {
        assertEquals( false, this.editableGroup.isDirty() );

        final SetAccountEditor editor = new SetAccountEditor( this.user );
        editor.edit( this.editableGroup );

        assertEquals( false, this.editableGroup.isDirty() );
    }

    @Test
    public void testSetRole_Wrong()
        throws Exception
    {
        assertEquals( false, this.editableRole.isDirty() );

        final SetAccountEditor editor = new SetAccountEditor( this.user );
        editor.edit( this.editableRole );

        assertEquals( false, this.editableRole.isDirty() );
    }
}
