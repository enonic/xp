package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalResponseSerializerStatusTest
{
    @Test
    void populateStatus_known()
    {
        final PortalResponse response = new PortalResponseSerializer( scriptWithStatus( 404 ) ).serialize();

        assertThat( response.getStatus() ).isEqualTo( HttpStatus.NOT_FOUND );
    }

    @Test
    void populateStatus_default()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final PortalResponse response = new PortalResponseSerializer( root, HttpStatus.CREATED ).serialize();

        assertThat( response.getStatus() ).isEqualTo( HttpStatus.CREATED );
    }

    @Test
    void populateStatus_unknown()
    {
        final PortalResponseSerializer serializer = new PortalResponseSerializer( scriptWithStatus( 299 ) );

        assertThatThrownBy( serializer::serialize ).isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "299" );
    }

    private static ScriptValue scriptWithStatus( final int status )
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue statusValue = mock( ScriptValue.class );
        when( root.getMember( "status" ) ).thenReturn( statusValue );
        when( statusValue.getValue( Integer.class ) ).thenReturn( status );
        return root;
    }
}
