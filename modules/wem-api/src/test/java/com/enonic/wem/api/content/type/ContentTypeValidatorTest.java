package com.enonic.wem.api.content.type;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.module.ModuleName;

import static org.junit.Assert.*;

public class ContentTypeValidatorTest
{

    private ContentType contentType;

    private ContentTypeValidator nonRecordedValidator;

    private ContentTypeValidator recordedValidator;

    private ContentTypeFetcher fetcher;

    @Before
    public void before()
    {
        contentType = ContentType.newContentType().
            module( ModuleName.from( "test" ) ).
            name( "MyType" ).build();
        fetcher = Mockito.mock( ContentTypeFetcher.class );
        nonRecordedValidator = ContentTypeValidator.
            newContentTypeValidator().
            contentTypeFetcher( fetcher ).
            recordExceptions( false ).build();
        recordedValidator = ContentTypeValidator.
            newContentTypeValidator().
            contentTypeFetcher( fetcher ).
            recordExceptions( true ).build();
    }

    @Test
    public void content_type_with_no_final_super_type()
    {
        ContentType child = ContentType.newContentType().
            module( ModuleName.from( "test" ) ).
            name( "MySubType" ).
            superType( contentType.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( contentType );
        nonRecordedValidator.validate( child );
        assertTrue( nonRecordedValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test(expected = CannotInheritFromFinalContentTypeException.class)
    public void content_type_with_final_super_type()
    {
        ContentType parent = ContentType.newContentType().module( ModuleName.from( "test" ) ).name( "MyParent" ).setFinal().build();
        ContentType child = ContentType.newContentType().module( ModuleName.from( "test" ) ).name( "MyChld" ).superType(
            parent.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( parent );
        nonRecordedValidator.validate( child );
    }

    @Test
    public void recorded_content_type_with_no_final_super_type()
    {
        ContentType child = ContentType.newContentType().
            module( ModuleName.from( "test" ) ).
            name( "MySubType" ).
            superType( contentType.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( contentType );
        recordedValidator.validate( child );
        assertTrue( recordedValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test
    public void recorded_content_type_with_final_super_type()
    {
        ContentType parent = ContentType.newContentType().name( "MyParent" ).module( ModuleName.from( "test" ) ).setFinal().build();
        ContentType child = ContentType.newContentType().name( "MyChld" ).module( ModuleName.from( "test" ) ).superType(
            parent.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( parent );
        recordedValidator.validate( child );
        assertEquals( 1, recordedValidator.getInvalidContentTypeExceptions().size() );
    }

}
