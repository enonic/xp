package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationErrorCodeTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ValidationErrorCode.class ).withNonnullFields( "applicationKey", "code" ).verify();
    }

    @Test
    void asString()
    {
        final ValidationErrorCode validationErrorCode = ValidationErrorCode.from( ApplicationKey.from( "someApp" ), "someCode" );
        assertEquals( "someApp:someCode", validationErrorCode.toString() );
    }

    @Test
    void from()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "someApp" );
        final ValidationErrorCode validationErrorCode = ValidationErrorCode.from( applicationKey, "someCode" );

        assertEquals( applicationKey, validationErrorCode.getApplicationKey() );
        assertEquals( "someCode", validationErrorCode.getCode() );
    }

    @Test
    void parse()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "someApp" );
        final ValidationErrorCode validationErrorCode = ValidationErrorCode.parse( "someApp:someCode" );

        assertEquals( applicationKey, validationErrorCode.getApplicationKey() );
        assertEquals( "someCode", validationErrorCode.getCode() );
    }

}