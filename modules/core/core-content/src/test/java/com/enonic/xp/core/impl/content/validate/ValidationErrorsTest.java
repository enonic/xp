package com.enonic.xp.core.impl.content.validate;

import org.junit.Test;

import com.enonic.xp.form.FormItemPath;

import static org.junit.Assert.*;

public class ValidationErrorsTest
{

    @Test
    public void validattionResult()
    {
        ValidationErrors errors1 =
            ValidationErrors.create().add( new DataValidationError( FormItemPath.from( "root" ), "errorMessage" ) ).build();
        ValidationErrors errors2 = ValidationErrors.empty();
        assertTrue( errors2.getValidationErrors().size() == 0 );
        assertNotEquals( errors1, errors2 );
        assertNotEquals( errors1.hashCode(), errors2.hashCode() );
        errors2 = ValidationErrors.create().addAll( errors1 ).build();
        assertEquals( errors1, errors2 );
    }

}
