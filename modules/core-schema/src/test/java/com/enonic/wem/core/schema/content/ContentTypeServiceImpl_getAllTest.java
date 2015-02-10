package com.enonic.wem.core.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;

import static com.enonic.wem.api.form.Form.newForm;
import static com.enonic.wem.api.form.Inline.newInline;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class ContentTypeServiceImpl_getAllTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void testGetAll()
    {
        final ContentType type1 = createContentType( "mymodule:my-contenttype-1", "DisplayName1" );
        final ContentType type2 = createContentType( "mymodule:my-contenttype-2", "DisplayName2" );

        register( type1, type2 );

        final ContentTypes result = this.service.getAll( new GetAllContentTypesParams() );
        assertEquals( 2, result.getSize() );

        verifyContentType( "mymodule:my-contenttype-1", "DisplayName1", result );
        verifyContentType( "mymodule:my-contenttype-2", "DisplayName2", result );

        unregister();

        final ContentTypes result2 = this.service.getAll( new GetAllContentTypesParams() );
        assertEquals( 0, result2.getSize() );
    }

    @Test
    public void testGetAll_inlines()
        throws Exception
    {
        final String contentTypeName = "mymodule:my-contenttype-1";

        final Mixin mixin = newMixin().name( "mymodule:my_mixin" ).
            addFormItem( newInput().
                name( "inputToBeMixedIn" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();

        final Form form = newForm().addFormItem( newInline( mixin ).build() ).build();

        Mockito.when( this.mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( contentTypeName ).
            displayName( "displayName" ).
            description( "description" ).
            form( form ).
            build();

        register( contentType );

        final GetAllContentTypesParams params = new GetAllContentTypesParams().inlinesToFormItems( true );
        final ContentTypes result = this.service.getAll( params );

        Mockito.verify( this.mixinService, Mockito.times( 1 ) ).getByName( Mockito.isA( MixinName.class ) );
        assertEquals( 1, result.getSize() );
        assertNotNull( result.get( 0 ).form().getInput( "inputToBeMixedIn" ) );
        assertNull( result.get( 0 ).form().getFormItem( "myMixin" ) );
    }
}
