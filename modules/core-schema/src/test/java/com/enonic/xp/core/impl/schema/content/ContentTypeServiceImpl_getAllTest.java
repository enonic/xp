package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.mixin.Mixin;

import static org.junit.Assert.*;

public class ContentTypeServiceImpl_getAllTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void testGetAll()
    {
        final ContentType type1 = createContentType( "myapplication:my-contenttype-1", "DisplayName1" );
        final ContentType type2 = createContentType( "myapplication:my-contenttype-2", "DisplayName2" );

        register( type1, type2 );

        final ContentTypes result = this.service.getAll( new GetAllContentTypesParams() );
        assertEquals( 2, result.getSize() );

        verifyContentType( "myapplication:my-contenttype-1", "DisplayName1", result );
        verifyContentType( "myapplication:my-contenttype-2", "DisplayName2", result );
    }

    @Test
    public void testGetAll_inlineMixins()
        throws Exception
    {
        final String contentTypeName = "myapplication:my-contenttype-1";

        final Mixin mixin = Mixin.create().name( "myapplication:my_mixin" ).
            addFormItem( Input.create().
                name( "inputToBeMixedIn" ).
                label( "Mixed in" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            build();

        final Form form = Form.create().addFormItem( InlineMixin.create( mixin ).build() ).build();

        final Form transformedForm = Form.create().addFormItem( Input.create().
            name( "inputToBeMixedIn" ).
            label( "Mixed in" ).
            inputType( InputTypeName.TEXT_LINE ).
            build() ).build();

        Mockito.when( this.mixinService.inlineFormItems( form ) ).thenReturn( transformedForm );

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( contentTypeName ).
            displayName( "displayName" ).
            description( "description" ).
            form( form ).
            build();

        register( contentType );

        final GetAllContentTypesParams params = new GetAllContentTypesParams().inlineMixinsToFormItems( true );
        final ContentTypes result = this.service.getAll( params );

        Mockito.verify( this.mixinService, Mockito.times( 1 ) ).inlineFormItems( Mockito.isA( Form.class ) );
        assertEquals( 1, result.getSize() );
        assertNotNull( result.get( 0 ).form().getInput( "inputToBeMixedIn" ) );
        assertNull( result.get( 0 ).form().getFormItem( "myMixin" ) );
    }
}
