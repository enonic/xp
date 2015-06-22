package com.enonic.xp.schema.content;

import java.time.Instant;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class ContentTypesTest
{

    @Test
    public void add_array()
    {
        ContentTypes contentTypes = ContentTypes.empty();

        ContentTypes newContentTypes =
            contentTypes.add( newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test1" ).build(),
                              newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test2" ).build() );

        assertEquals( 0, contentTypes.getSize() );
        assertEquals( 2, newContentTypes.getSize() );
    }

    @Test
    public void add_iterable()
    {
        ContentTypes contentTypes = ContentTypes.empty();

        List<ContentType> contentTypeList =
            Lists.newArrayList( newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test1" ).build(),
                                newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test2" ).build() );

        ContentTypes newContentTypes = contentTypes.add( contentTypeList );

        assertEquals( 0, contentTypes.getSize() );
        assertEquals( 2, newContentTypes.getSize() );
    }

    @Test
    public void contentTypes()
    {
        ContentType.Builder builder = newContentType().name( ContentTypeName.media() ).form(
            ContentTypeForms.PAGE_TEMPLATE ).setAbstract().setFinal().allowChildContent( true ).setBuiltIn().contentDisplayNameScript(
            "contentDisplayNameScript" ).metadata( null ).displayName( "displayName" ).description( "description" ).modifiedTime(
            Instant.now() ).createdTime( Instant.now() ).creator( PrincipalKey.ofAnonymous() ).modifier( PrincipalKey.ofAnonymous() );
        ContentType contentType = builder.build();
        ContentTypes contentTypes = ContentTypes.newContentTypes().add( contentType ).build();
        assertTrue( contentTypes.getNames().contains( ContentTypeName.media() ) );
        assertTrue( ContentTypes.empty().getSize() == 0 );
        assertTrue( ContentTypes.from( contentType ).getSize() == 1 );
        assertNotNull( contentTypes.getContentType( contentType.getName() ) );
    }

    @Test
    public void from()
    {
        ContentTypes contentTypes =
            ContentTypes.from( newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test1" ).build(),
                               newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test2" ).build() );

        List<ContentType> contentTypeList =
            Lists.newArrayList( newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test1" ).build(),
                                newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test2" ).build(),
                                newContentType().superType( ContentTypeName.structured() ).name( "mymodule:test3" ).build() );

        assertEquals( 2, contentTypes.getSize() );
        assertEquals( 2, ContentTypes.from( contentTypes ).getSize() );
        assertEquals( 3, ContentTypes.from( contentTypeList ).getSize() );
    }
}