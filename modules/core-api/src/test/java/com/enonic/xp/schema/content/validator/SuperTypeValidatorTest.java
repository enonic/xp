package com.enonic.xp.schema.content.validator;

import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import static com.enonic.xp.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class SuperTypeValidatorTest
{

    @Test
    public void content_type_passes_validation()
    {
        ContentType contentType = newContentType().
            name( ContentTypeName.documentMedia() ).
            superType( ContentTypeName.media() ).
            build();

        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator()
            .contentTypeService( contentTypeService ).build();
        validator.validate( ContentTypeName.documentMedia(), ContentTypeName.media() );
    }

    @Test
    public void content_type_super_is_null()
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

        ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator()
            .contentTypeService( contentTypeService ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( null );
        validator.validate( ContentTypeName.documentMedia(), ContentTypeName.media() );

        assertNotNull( validator.getResult().getFirst().getErrorMessage() );
    }

    @Test
    public void validationResult()
    {
        ContentTypeValidationResult validationResult1 = ContentTypeValidationResult.from(new ContentTypeValidationError("superType not found: superTypeName", ContentTypeName.media()));
        ContentTypeValidationResult validationResult2 = ContentTypeValidationResult.from(new ArrayList<ContentTypeValidationError>() {{ add(new ContentTypeValidationError("superType not found: superTypeName", ContentTypeName.media())); }});
        ContentTypeValidationResult validationResult3 = ContentTypeValidationResult.empty();
        assertNotEquals( validationResult1, validationResult2 );
        assertNotEquals( validationResult1.hashCode(), validationResult2.hashCode() );
        assertFalse( validationResult3.hasErrors() );
        assertNotEquals( validationResult2.hashCode(), validationResult3.hashCode() );
        assertNotEquals( validationResult2.toString(), validationResult3.toString() );
    }




}
