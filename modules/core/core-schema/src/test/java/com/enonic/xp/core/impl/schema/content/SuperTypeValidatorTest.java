package com.enonic.xp.core.impl.schema.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.ContentTypeValidationError;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SuperTypeValidatorTest
{

    @Test
    public void content_type_passes_validation()
    {
        ContentType contentType = ContentType.create().name( ContentTypeName.documentMedia() ).superType( ContentTypeName.media() ).build();

        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.create().contentTypeService( contentTypeService ).build();
        validator.validate( ContentTypeName.documentMedia(), ContentTypeName.media() );
    }

    @Test
    public void content_type_super_is_null()
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

        ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.create().contentTypeService( contentTypeService ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );
        validator.validate( ContentTypeName.documentMedia(), ContentTypeName.media() );

        assertNotNull( validator.getResult().getFirst().getErrorMessage() );
    }

    @Test
    public void validationResult()
    {
        ContentTypeValidationResult validationResult1 = ContentTypeValidationResult.from(
            new ContentTypeValidationError( "superType not found: superTypeName", ContentTypeName.media() ) );
        ContentTypeValidationResult validationResult2 = ContentTypeValidationResult.from(
            new ContentTypeValidationError( "superType not found: superTypeName", ContentTypeName.media() ) );
        ContentTypeValidationResult validationResult3 = ContentTypeValidationResult.from();
        assertNotEquals( validationResult1, validationResult2 );
        assertFalse( validationResult3.hasErrors() );
        assertNotEquals( validationResult2.toString(), validationResult3.toString() );
    }
}
