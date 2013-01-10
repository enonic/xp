package com.enonic.wem.api.content.type.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.module.ModuleName;

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
            module( ModuleName.from( "test" ) ).
            name( "MyType" ).build();
        fetcher = Mockito.mock( ContentTypeFetcher.class );

        recordedValidator = ContentTypeValidator.
            newContentTypeValidator().
            contentTypeFetcher( fetcher ).build();
    }

    @Test
    public void recorded_content_type_with_no_final_super_type()
    {
        ContentType child = ContentType.newContentType().
            module( ModuleName.from( "test" ) ).
            name( "MyType" ).
            superType( contentType.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( contentType );
        recordedValidator.validate( child );
        assertFalse( recordedValidator.getResult().hasErrors() );
    }

    @Test
    public void recorded_content_type_with_final_super_type()
    {
        ContentType parent = ContentType.newContentType().name( "MyFinalParent" ).module( ModuleName.from( "test" ) ).setFinal().build();
        ContentType child = ContentType.newContentType().name( "MyChild" ).module( ModuleName.from( "test" ) ).superType(
            parent.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( parent );
        recordedValidator.validate( child );
        assertTrue( recordedValidator.getResult().hasErrors() );
        assertEquals( "Invalid content type: [test:MyChild]: Cannot inherit from a final ContentType: test:MyFinalParent",
                      recordedValidator.getResult().getFirst().getErrorMessage() );
    }

}
