package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.Assert.*;

public class ModifyProfileHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testExamples()
    {
        final User user = TestDataFixtures.getTestUser();

        Mockito.when( securityService.getUser( Mockito.any() ) ).
            thenReturn( Optional.of( user ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).
            thenAnswer( invocationOnMock -> invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0], user ) );

        runScript( "/site/lib/xp/examples/auth/modifyProfile.js" );
    }

    @Test
    public void keep_original_value_types_when_not_touched()
    {

        final User user = TestDataFixtures.getTestUserWithProfile();

        Mockito.when( securityService.getUser( Mockito.any() ) ).
            thenReturn( Optional.of( user ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).
            thenAnswer( invocationOnMock -> {
                final User editedUser = invokeUpdate( (UpdateUserParams) invocationOnMock.getArguments()[0], user );
                final PropertySet profile = editedUser.getProfile().getSet( "myApp" );

                assertTrue( profile.getProperty( "untouchedString" ).getType().equals( ValueTypes.STRING ) );
                assertTrue( profile.getProperty( "untouchedBoolean" ).getType().equals( ValueTypes.BOOLEAN ) );
                assertTrue( profile.getProperty( "untouchedDouble" ).getType().equals( ValueTypes.DOUBLE ) );
                assertTrue( profile.getProperty( "untouchedLong" ).getType().equals( ValueTypes.LONG ) );
                assertTrue( profile.getProperty( "untouchedLink" ).getType().equals( ValueTypes.LINK ) );
                assertTrue( profile.getProperty( "untouchedInstant" ).getType().equals( ValueTypes.DATE_TIME ) );
                assertTrue( profile.getProperty( "untouchedGeoPoint" ).getType().equals( ValueTypes.GEO_POINT ) );
                assertTrue( profile.getProperty( "untouchedLocalDate" ).getType().equals( ValueTypes.LOCAL_DATE ) );
                assertTrue( profile.getProperty( "untouchedReference" ).getType().equals( ValueTypes.REFERENCE ) );
                assertTrue( profile.getProperty( "untouchedBinaryRef" ).getType().equals( ValueTypes.BINARY_REFERENCE ) );

                return editedUser;
            } );

        runScript( "/site/test/modifyProfile-test.js" );

    }

    private User invokeUpdate( final UpdateUserParams params, final User user )
    {
        final UserEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final EditableUser editable = new EditableUser( user );

        editor.edit( editable );
        return editable.build();
    }
}
