package com.enonic.xp.schema.content;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentTypesTest
{
    private static final Form PAGE_TEMPLATE = Form.create().
        addFormItem( Input.create().
            name( "supports" ).
            label( "Supports" ).
            helpText( "Choose which content types this page template supports" ).
            inputType( InputTypeName.CONTENT_TYPE_FILTER ).
            required( true ).
            multiple( true ).
            build() ).
        build();

    @Test
    void contentTypes()
    {
        ContentType.Builder builder =
            ContentType.
                create().
                name( ContentTypeName.media() ).
                form( PAGE_TEMPLATE ).
                setAbstract().
                setFinal().
                allowChildContent( true ).
                setBuiltIn().
                displayNameExpression( "displayNameExpression" ).
                displayName( "displayName" ).
                description( "description" ).
                modifiedTime( Instant.now() ).
                createdTime( Instant.now() ).
                creator( PrincipalKey.ofAnonymous() ).
                modifier( PrincipalKey.ofAnonymous() );
        ContentType contentType = builder.build();
        assertTrue( ContentTypes.empty().isEmpty() );
        assertEquals( 1, ContentTypes.from( contentType ).getSize() );
    }

    @Test
    void from()
    {
        ContentTypes contentTypes =
            ContentTypes.from( ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test1" ).build(),
                               ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test2" ).build() );

        List<ContentType> contentTypeList =
            List.of( ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test1" ).build(),
                     ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test2" ).build(),
                     ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test3" ).build() );

        assertEquals( 2, contentTypes.getSize() );
        assertEquals( 2, ContentTypes.from( contentTypes ).getSize() );
        assertEquals( 3, ContentTypes.from( contentTypeList ).getSize() );
    }
}
