package com.enonic.xp.content;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationErrorsTest
{
    @Test
    public void empty()
    {
        final ValidationErrors errors = ValidationErrors.create().build();
        assertFalse( errors.hasErrors() );
    }

    @Test
    public void not_empty()
    {
        final ValidationErrors errors = ValidationErrors.create().add( ValidationError.generalError( "SOME_CODE" ).build() ).build();
        assertTrue( errors.hasErrors() );
    }

    @Test
    public void stream()
    {
        final ValidationError error1 = ValidationError.generalError( "SOME_CODE" ).build();
        final ValidationError error2 = ValidationError.generalError( "SOME_CODE_2" ).build();
        final ValidationError error3 = ValidationError.generalError( "SOME_CODE_3" ).build();
        final ValidationError error4 = ValidationError.generalError( "SOME_CODE_4" ).build();

        final ValidationErrors errors = ValidationErrors.create().add( error1 ).add( error2 ).addAll( List.of( error3, error4 ) ).build();
        assertThat( errors.stream() ).containsExactly( error1, error2, error3, error4 );
    }
}