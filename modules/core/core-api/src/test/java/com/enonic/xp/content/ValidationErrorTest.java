package com.enonic.xp.content;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationErrorTest
{
    @Test
    void generalError()
    {
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) ).build();
        assertThat( validationError ).isInstanceOf( ValidationError.class )
            .extracting( ValidationError::getErrorCode )
            .asString()
            .isEqualTo( "system:ERR_CODE" );
    }

    @Test
    void dataError()
    {
        final ValidationError validationError =
            ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ), PropertyPath.from( "a.b" ) ).build();
        assertThat( validationError ).isInstanceOf( DataValidationError.class )
            .extracting( ValidationError::getErrorCode )
            .asString()
            .isEqualTo( "system:ERR_CODE" );
        assertEquals( PropertyPath.from( "a.b" ), ( (DataValidationError) validationError ).getPropertyPath() );
    }

    @Test
    void attachmentError()
    {
        final ValidationError validationError =
            ValidationError.attachmentError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ), BinaryReference.from( "f" ) )
                .build();
        assertThat( validationError ).isInstanceOf( AttachmentValidationError.class )
            .extracting( ValidationError::getErrorCode )
            .asString()
            .isEqualTo( "system:ERR_CODE" );
        assertEquals( BinaryReference.from( "f" ), ( (AttachmentValidationError) validationError ).getAttachment() );
    }

    @Test
    void messageFormat()
    {
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) )
                .message( "{0}" )
                .args( "a" )
                .build();
        assertEquals( "a", validationError.getMessage() );
    }

    @Test
    void skipFormat()
    {
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) )
                .message( "{0}", true )
                .args( "a" )
                .build();
        assertEquals( "{0}", validationError.getMessage() );
    }

    @Test
    void dateArgConvertedToTimestamp()
    {
        final Date date = new Date();
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) )
                .message( "{0, date}" )
                .args( date )
                .build();
        assertEquals( DateFormat.getDateInstance( DateFormat.DEFAULT ).format( date ), validationError.getMessage() );
        assertEquals( date.getTime(), validationError.getArgs().get( 0 ) );
    }

    @Test
    void classArgConvertedToString()
    {
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) )
                .message( "{0}" )
                .args( PropertyPath.from( "a.b" ) )
                .build();
        assertEquals( "a.b", validationError.getMessage() );
        assertEquals( "a.b", validationError.getArgs().get( 0 ) );
    }

    @Test
    void dateArgAsNumberSupported()
    {
        final Date date = new Date();
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) )
                .message( "{0, date}" )
                .args( date.getTime() )
                .build();
        assertEquals( DateFormat.getDateInstance( DateFormat.DEFAULT ).format( date ), validationError.getMessage() );
    }

    @Test
    void fields()
    {
        final ValidationError validationError =
            ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "ERR_CODE" ) )
                .i18n( "some.message" )
                .args( "a", 1 )
                .message( "{0}", true )
                .build();
        assertAll( () -> assertEquals( "some.message", validationError.getI18n() ),
                   () -> assertEquals( Arrays.asList( "a", 1 ), validationError.getArgs() ),
                   () -> assertEquals( "{0}", validationError.getMessage() ) );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ValidationError.class ).usingGetClass().withNonnullFields( "args" ).verify();

        EqualsVerifier.forClass( AttachmentValidationError.class )
            .withRedefinedSuperclass()
            .withNonnullFields( "args", "attachment" )
            .verify();

        EqualsVerifier.forClass( DataValidationError.class )
            .withRedefinedSuperclass()
            .withPrefabValues( PropertyPath.class, PropertyPath.from( "red" ), PropertyPath.from( "blue" ) )
            .withNonnullFields( "args", "propertyPath" )
            .verify();
    }
}
