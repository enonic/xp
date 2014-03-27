package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetAllContentTypes;
import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.form.FormItemsDataSerializer;

import static com.enonic.wem.api.form.Form.newForm;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class GetAllContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetAllContentTypesHandler handler;

    private MixinService mixinService;

    private static final FormItemsDataSerializer SERIALIZER_FOR_FORM_ITEM_TO_DATA = new FormItemsDataSerializer();

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinService = Mockito.mock( MixinService.class );

        handler = new GetAllContentTypesHandler();
        handler.setContext( this.context );
        handler.setMixinService( this.mixinService );
    }

    @Ignore // Does not work atm because of rewriting of client to instanticate handler
    @Test
    public void handle()
        throws Exception
    {
        // Setup:
        final String contentType1Name = "my-contenttype-1";
        final String contentType2Name = "my-contenttype-2";

        final Nodes nodes = Nodes.newNodes().
            add( Node.newNode().
                name( NodeName.from( contentType1Name ) ).
                id( EntityId.from( "123" ) ).
                property( "displayName", "DisplayName" ).
                property( "description", "Description" ).
                build() ).
            add( Node.newNode().
                name( NodeName.from( contentType2Name ) ).
                id( EntityId.from( "234" ) ).
                property( "displayName", "DisplayName2" ).
                property( "description", "Description2" ).
                build() ).
            build();

        Mockito.when( nodeService.getByParent( Mockito.isA( GetNodesByParentParams.class ) ) ).thenReturn( nodes );

        // Exercise:
        GetAllContentTypes command = Commands.contentType().get().all();
        this.handler.setCommand( command );
        this.handler.handle();

        // Verify:
        final ContentTypes result = command.getResult();
        assertEquals( 2, result.getSize() );

        verifyContentType( contentType1Name, "DisplayName", "Description", result );
        verifyContentType( contentType2Name, "DisplayName2", "Description2", result );

    }


    @Ignore // Does not work atm because of rewriting of client to instanticate handler
    @Test
    public void handle_given_mixin()
        throws Exception
    {
        final Mixin mixin = newMixin().name( "my_mixin" ).
            addFormItem( newInput().
                name( "inputToBeMixedIn" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();

        final Form form = newForm().addFormItem( newMixinReference( mixin ).name( "myMixin" ).build() ).build();
        final RootDataSet rootDataSetWithForm = createRootDataSetWithForm( form );

        final Nodes nodes = Nodes.newNodes().
            add( Node.newNode().
                name( NodeName.from( "my-contenttype-1" ) ).
                id( EntityId.from( "1" ) ).
                rootDataSet( rootDataSetWithForm ).
                build() ).
            build();

        Mockito.when( mixinService.getByName( Mockito.isA( GetMixinParams.class ) ) ).thenReturn( mixin );
        Mockito.when( nodeService.getByParent( Mockito.isA( GetNodesByParentParams.class ) ) ).thenReturn( nodes );

        // Exercise:
        GetAllContentTypes command = Commands.contentType().get().all().mixinReferencesToFormItems( true );
        this.handler.setCommand( command );
        this.handler.handle();

        // One invocation for each contentType with mixin-reference
        Mockito.verify( mixinService, Mockito.times( 1 ) ).getByName( Mockito.isA( GetMixinParams.class ) );
        final ContentTypes result = command.getResult();
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
}
