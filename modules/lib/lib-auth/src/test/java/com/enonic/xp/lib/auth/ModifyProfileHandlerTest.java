package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModifyProfileHandlerTest
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
    void testExamples()
    {
        final User user = TestDataFixtures.getTestUser();

        Mockito.when( securityService.getUser( Mockito.any() ) ).thenReturn( Optional.of( user ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) )
            .thenAnswer( invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), user ) );

        runScript( "/lib/xp/examples/auth/modifyProfile.js" );
    }

    @Test
    void keep_original_value_types_when_not_touched()
    {

        final User user = TestDataFixtures.getTestUserWithProfile();

        Mockito.when( securityService.getUser( Mockito.any() ) ).thenReturn( Optional.of( user ) );

        Mockito.when( this.securityService.updateUser( Mockito.isA( UpdateUserParams.class ) ) ).thenAnswer( invocationOnMock -> {
            final User editedUser = invokeUpdate( invocationOnMock.getArgument( 0 ), user );
            final PropertySet profile = editedUser.getProfile().getSet( "myApp" );

            assertEquals( ValueTypes.STRING, profile.getProperty( "untouchedString" ).getType() );
            assertEquals( ValueTypes.BOOLEAN, profile.getProperty( "untouchedBoolean" ).getType() );
            assertEquals( ValueTypes.DOUBLE, profile.getProperty( "untouchedDouble" ).getType() );
            assertEquals( ValueTypes.LONG, profile.getProperty( "untouchedLong" ).getType() );
            assertEquals( ValueTypes.LINK, profile.getProperty( "untouchedLink" ).getType() );
            assertEquals( ValueTypes.DATE_TIME, profile.getProperty( "untouchedInstant" ).getType() );
            assertEquals( ValueTypes.GEO_POINT, profile.getProperty( "untouchedGeoPoint" ).getType() );
            assertEquals( ValueTypes.LOCAL_DATE, profile.getProperty( "untouchedLocalDate" ).getType() );
            assertEquals( ValueTypes.REFERENCE, profile.getProperty( "untouchedReference" ).getType() );
            assertEquals( ValueTypes.BINARY_REFERENCE, profile.getProperty( "untouchedBinaryRef" ).getType() );

            return editedUser;
        } );

        runScript( "/test/modifyProfile-test.js" );

    }

    private User invokeUpdate( final UpdateUserParams params, final User user )
    {
        final UserEditor editor = params.getEditor();
        assertNotNull( editor );

        final EditableUser editable = new EditableUser( user );

        editor.edit( editable );
        return editable.build();
    }
}
