package com.enonic.xp.schema.content.validator;

import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.schema.content.ContentType.newContentType;
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
