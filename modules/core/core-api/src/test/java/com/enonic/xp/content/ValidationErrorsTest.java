package com.enonic.xp.content;

import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationErrorsTest
{
    @Test
    void empty()
    {
        final ValidationErrors errors = ValidationErrors.create().build();
        assertFalse( errors.hasErrors() );
    }

    @Test
    void not_empty()
    {
        final ValidationErrors errors = ValidationErrors.create()
            .add( ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE" ) ).build() )
            .build();
        assertTrue( errors.hasErrors() );
    }

    @Test
    void stream()
    {
        final ValidationError error1 =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE" ) ).build();
        final ValidationError error2 =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE_2" ) ).build();
        final ValidationError error3 =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE_3" ) ).build();
        final ValidationError error4 =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE_4" ) ).build();

        final ValidationErrors errors = ValidationErrors.create().add( error1 ).add( error2 ).addAll( List.of( error3, error4 ) ).build();
        assertThat( errors.stream() ).containsExactly( error1, error2, error3, error4 );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ValidationErrors.class ).withNonnullFields( "errors" ).verify();
    }

}
