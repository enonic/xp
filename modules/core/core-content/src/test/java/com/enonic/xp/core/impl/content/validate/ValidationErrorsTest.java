package com.enonic.xp.core.impl.content.validate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationErrorsTest
{

    @Test
    public void validationResult()
    {
        ValidationErrors errors1 = ValidationErrors.create()
            .add( new DataValidationError( PropertyPath.from( "root" ), "CUSTOM_ERROR_CODE", "errorMessage" ) )
            .build();
        ValidationErrors errors2 = ValidationErrors.create().build();
        assertFalse( errors2.isNotEmpty() );
        assertNotEquals( errors1, errors2 );
        assertNotEquals( errors1.hashCode(), errors2.hashCode() );
        errors2 = ValidationErrors.create().addAll( errors1.getList() ).build();
        assertEquals( errors1, errors2 );
    }

}
