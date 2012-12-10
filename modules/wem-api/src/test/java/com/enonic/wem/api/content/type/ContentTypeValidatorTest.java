package com.enonic.wem.api.content.type;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.module.Module;

import static org.junit.Assert.*;

public class ContentTypeValidatorTest
{

    private ContentType contentType;

    private ContentTypeValidator contentTypeValidator;

    private ContentTypeValidator recordedValidator;

    private ContentTypeFetcher fetcher;

    @Before
    public void before()
    {
        contentType = ContentType.newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MyType" ).build();
        fetcher = Mockito.mock( ContentTypeFetcher.class );
        contentTypeValidator = ContentTypeValidator.
            newContentTypeValidator().
            superTypeFetcher( fetcher ).
            recordExceptions( false ).build();
        recordedValidator = ContentTypeValidator.
            newContentTypeValidator().
            superTypeFetcher( fetcher ).
            recordExceptions( true ).build();
    }

    @Test
    public void test_content_type_with_no_super_type()
    {
        contentTypeValidator.validate( contentType );
        assert ( contentTypeValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test
    public void test_content_type_with_no_final_super_type()
    {
        ContentType child = ContentType.newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MySubType" ).
            superType( contentType.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( contentType );
        contentTypeValidator.validate( child );
        assert ( contentTypeValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test(expected = CannotInheritFromFinalContentTypeException.class)
    public void test_content_type_with_final_super_type()
    {
        ContentType parent = ContentType.newContentType().module( Module.SYSTEM.getName() ).name( "MyParent" ).setFinal().build();
        ContentType child =
            ContentType.newContentType().module( Module.SYSTEM.getName() ).name( "MyChld" ).superType( parent.getSuperType() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( parent );
        contentTypeValidator.validate( child );
        assert ( contentTypeValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test
    public void test_recorded_content_type_with_no_super_type()
    {
        recordedValidator.validate( contentType );
        assert ( recordedValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test
    public void test_recorded_content_type_with_no_final_super_type()
    {
        ContentType child = ContentType.newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MySubType" ).
            superType( contentType.getQualifiedName() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( contentType );
        recordedValidator.validate( child );
        assert ( recordedValidator.getInvalidContentTypeExceptions().isEmpty() );
    }

    @Test
    public void test_recorded_content_type_with_final_super_type()
    {
        ContentType parent = ContentType.newContentType().module( Module.SYSTEM.getName() ).name( "MyParent" ).setFinal().build();
        ContentType child =
            ContentType.newContentType().module( Module.SYSTEM.getName() ).name( "MyChld" ).superType( parent.getSuperType() ).build();
        Mockito.when( fetcher.getContentType( child.getSuperType() ) ).thenReturn( parent );
        recordedValidator.validate( child );
        assertEquals( 1, recordedValidator.getInvalidContentTypeExceptions().size() );
    }

}
