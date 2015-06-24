package com.enonic.xp.schema.content.validator;

import org.junit.Test;

import com.enonic.xp.form.FormItemPath;

import static org.junit.Assert.*;

public class DataValidationErrorsTest
{

    @Test
    public void validattionResult()
    {
        DataValidationErrors errors1 = DataValidationErrors.create().add(new DataValidationError( FormItemPath.from("root"), "errorMesage" )).build();
        DataValidationErrors errors2 = DataValidationErrors.empty();
        assertTrue( errors2.getValidationErrors().size() == 0 );
        assertNotEquals( errors1, errors2 );
        assertNotEquals( errors1.hashCode(), errors2.hashCode() );
        errors2 = DataValidationErrors.create().addAll( errors1 ).build();
        assertEquals( errors1, errors2 );
    }

}
