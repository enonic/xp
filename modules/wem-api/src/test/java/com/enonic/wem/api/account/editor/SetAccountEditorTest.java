package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

public class SetAccountEditorTest
{
    private UserAccount user;

    private GroupAccount group;

    private RoleAccount role;

    private EditableUserAccount editableUser;

    private EditableGroupAccount editableGroup;

    private EditableRoleAccount editableRole;

    private byte[] photo;

    private AccountKeySet members;

    @Before
    public void setUp()
    {
        this.user = Mockito.mock( UserAccount.class );
        this.group = Mockito.mock( GroupAccount.class );
        this.role = Mockito.mock( RoleAccount.class );
        this.editableUser = Mockito.mock( EditableUserAccount.class );
        this.editableGroup = Mockito.mock( EditableGroupAccount.class );
        this.editableRole = Mockito.mock( EditableRoleAccount.class );

        this.photo = new byte[10];
        this.members = AccountKeySet.from( "role:other:admin" );

        Mockito.when( this.user.getEmail() ).thenReturn( "dummy@other.com" );
        Mockito.when( this.user.getDisplayName() ).thenReturn( "Dummy User" );
        Mockito.when( this.user.getPhoto() ).thenReturn( photo );

        Mockito.when( this.group.getDisplayName() ).thenReturn( "Dummy Group" );
        Mockito.when( this.group.getMembers() ).thenReturn( this.members );

        Mockito.when( this.role.getDisplayName() ).thenReturn( "Dummy Role" );
        Mockito.when( this.role.getMembers() ).thenReturn( this.members );
    }

    @Test
    public void testSetUser()
        throws Exception
    {
        final SetAccountEditor editor = new SetAccountEditor( this.user );
        editor.edit( this.editableUser );

        Mockito.verify( this.editableUser, Mockito.times( 1 ) ).setEmail( "dummy@other.com" );
        Mockito.verify( this.editableUser, Mockito.times( 1 ) ).setDisplayName( "Dummy User" );
        Mockito.verify( this.editableUser, Mockito.times( 1 ) ).setPhoto( this.photo );
    }

    @Test
    public void testSetGroup()
        throws Exception
    {
        final SetAccountEditor editor = new SetAccountEditor( this.group );
        editor.edit( this.editableGroup );

        Mockito.verify( this.editableGroup, Mockito.times( 1 ) ).setDisplayName( "Dummy Group" );
        Mockito.verify( this.editableGroup, Mockito.times( 1 ) ).setMembers( this.members );
    }

    @Test
    public void testSetRole()
        throws Exception
    {
        final SetAccountEditor editor = new SetAccountEditor( this.role );
        editor.edit( this.editableRole );

        Mockito.verify( this.editableRole, Mockito.times( 1 ) ).setDisplayName( "Dummy Role" );
        Mockito.verify( this.editableRole, Mockito.times( 1 ) ).setMembers( this.members );
    }

    @Test
    public void testSetUser_Wrong()
        throws Exception
    {
        final SetAccountEditor editor = new SetAccountEditor( this.group );
        editor.edit( this.editableUser );

        Mockito.verify( this.editableUser, Mockito.times( 0 ) ).setDisplayName( Mockito.anyString() );
    }

    @Test
    public void testSetGroup_Wrong()
        throws Exception
    {
        final SetAccountEditor editor = new SetAccountEditor( this.user );
        editor.edit( this.editableGroup );

        Mockito.verify( this.editableGroup, Mockito.times( 0 ) ).setDisplayName( Mockito.anyString() );
    }

    @Test
    public void testSetRole_Wrong()
        throws Exception
    {
        final SetAccountEditor editor = new SetAccountEditor( this.user );
        editor.edit( this.editableRole );

        Mockito.verify( this.editableRole, Mockito.times( 0 ) ).setDisplayName( Mockito.anyString() );
    }
}
