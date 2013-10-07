package com.enonic.wem.api.schema.content.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeFetcher;

import static org.junit.Assert.*;

public class ContentTypeValidatorTest
{
    private ContentType contentType;

    private ContentTypeValidator recordedValidator;

    private ContentTypeFetcher fetcher;

    @Before
    public void before()
    {
        contentType = ContentType.newContentType().
            name( "my_type" ).build();
        fetcher = Mockito.mock( ContentTypeFetcher.class );

        recordedValidator = ContentTypeValidator.
            newContentTypeValidator().
            contentTypeFetcher( fetcher ).build();
    }

    @Test
    public void recorded_content_type_with_no_final_super_type()
    {
        ContentType child = ContentType.newContentType().
            name( "my_type" ).
            superType( contentType.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( contentType );
        recordedValidator.validate( child );
        assertFalse( recordedValidator.getResult().hasErrors() );
    }

    @Test
    public void recorded_content_type_with_final_super_type()
    {
        ContentType parent = ContentType.newContentType().name( "my_final_parent" ).setFinal().build();
        ContentType child = ContentType.newContentType().name( "my_child" ).superType( parent.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( parent );
        recordedValidator.validate( child );
        assertTrue( recordedValidator.getResult().hasErrors() );
        assertEquals( "Invalid content type: [my_child]: Cannot inherit from a final ContentType: my_final_parent",
                      recordedValidator.getResult().getFirst().getErrorMessage() );
    }

}
