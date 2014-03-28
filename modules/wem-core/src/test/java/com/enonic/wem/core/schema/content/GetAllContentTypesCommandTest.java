package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.form.FormItemsDataSerializer;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.form.Form.newForm;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class GetAllContentTypesCommandTest
    extends AbstractCommandHandlerTest
{
    private GetAllContentTypesCommand command;

    private MixinService mixinService;

    private ContentTypeDao contentTypeDao;

    private static final FormItemsDataSerializer SERIALIZER_FOR_FORM_ITEM_TO_DATA = new FormItemsDataSerializer();

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinService = Mockito.mock( MixinService.class );
        contentTypeDao = Mockito.mock( ContentTypeDao.class );

        command = new GetAllContentTypesCommand().mixinService( this.mixinService ).contentTypeDao( this.contentTypeDao );
    }

    @Test
    public void handle()
        throws Exception
    {
        // Setup:
        final String contentType1Name = "my-contenttype-1";
        final String contentType2Name = "my-contenttype-2";

        final String displayName1 = "DisplayName";
        final String displayName2 = "DisplayName2";

        final String description1 = "Description";
        final String description2 = "Description2";

        final ContentTypes allContentTypes = ContentTypes.from(
            createContentType( contentType1Name, displayName1, description1 ),
            createContentType( contentType2Name, displayName2, description2 ) );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        // exercise
        GetAllContentTypesParams params = new GetAllContentTypesParams();
        final ContentTypes result = this.command.params( params ).execute();

        // verify
        assertEquals( 2, result.getSize() );

        verifyContentType( contentType1Name, displayName1, description1, result );
        verifyContentType( contentType2Name, displayName2, description2, result );
    }

    @Test
    public void handle_given_mixin()
        throws Exception
    {
        final String contentTypeName = "my-contenttype-1";

        final Mixin mixin = newMixin().name( "my_mixin" ).
            addFormItem( newInput().
                name( "inputToBeMixedIn" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();

        final Form form = newForm().addFormItem( newMixinReference( mixin ).name( "myMixin" ).build() ).build();
        final RootDataSet rootDataSetWithForm = createRootDataSetWithForm( form );

        Mockito.when( mixinService.getByName( Mockito.isA( GetMixinParams.class ) ) ).thenReturn( mixin );

        final ContentType contentType = ContentType.
            newContentType().
            name( contentTypeName ).
            displayName( "displayName" ).
            description( "description" ).
            form( form ).
            build();

        final ContentTypes allContentTypes = ContentTypes.from( contentType );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        // Exercise:
        final GetAllContentTypesParams params = new GetAllContentTypesParams().mixinReferencesToFormItems( true );
        final ContentTypes result = this.command.params( params ).execute();

        // One invocation for each contentType with mixin-reference
        Mockito.verify( mixinService, Mockito.times( 1 ) ).getByName( Mockito.isA( GetMixinParams.class ) );
        assertEquals( 1, result.getSize() );
        assertNotNull( result.get( 0 ).form().getInput( "inputToBeMixedIn" ) );
        assertNull( result.get( 0 ).form().getFormItem( "myMixin" ) );
    }

    private RootDataSet createRootDataSetWithForm( final Form form )
    {
        final RootDataSet rootDataSet = new RootDataSet();

        final DataSet formAsDataSet = new DataSet( "form" );
        final DataSet formItems = new DataSet( "formItems" );
        formAsDataSet.add( formItems );

        for ( Data data : SERIALIZER_FOR_FORM_ITEM_TO_DATA.toData( form.getFormItems() ) )
        {
            formItems.add( data );
        }
        rootDataSet.add( formAsDataSet );

        return rootDataSet;
    }

    private void verifyContentType( final String contentTypeName, final String displayName, final String description, final ContentTypes result )
    {
        final ContentType contentType = result.getContentType( ContentTypeName.from( contentTypeName ) );
        assertNotNull( contentType );
        assertEquals( contentTypeName, contentType.getName().toString() );
        assertEquals( displayName, contentType.getDisplayName() );
        assertEquals( description, contentType.getDescription() );
    }

    private ContentType createContentType( final String name, final String displayName, final String description )
    {
        return ContentType.newContentType().displayName( displayName ).name( name ).description( description ).build();
    }
}
