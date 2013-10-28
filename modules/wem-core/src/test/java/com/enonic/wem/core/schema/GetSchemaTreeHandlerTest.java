package com.enonic.wem.core.schema;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.GetSchemaTree;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class GetSchemaTreeHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetSchemaTreeHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new GetSchemaTreeHandler();
        handler.setContext( this.context );
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void getSchemaTree()
        throws Exception
    {
        // setup
        final ContentType unstructuredContentType = newContentType().
            name( ContentTypeName.structured() ).
            builtIn( true ).
            displayName( "Unstructured" ).
            setFinal( false ).
            setAbstract( false ).
            build();

        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( unstructuredContentType.getQualifiedName() ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType, unstructuredContentType );
        Mockito.when( contentTypeDao.selectAll( any( Session.class ) ) ).thenReturn( contentTypes );

        final FormItemSet formItemSet = newFormItemSet().
            name( "address" ).
            addFormItem( newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).
            build();
        final Mixin mixin = newMixin().name( "address" ).
            addFormItem( formItemSet ).
            build();
        final Mixins mixins = Mixins.from( mixin );
        Mockito.when( client.execute( Mockito.isA( GetMixins.class ) ) ).thenReturn( mixins );

        final RelationshipType relationshipType = newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( client.execute( Mockito.isA( GetRelationshipTypes.class ) ) ).thenReturn( relationshipTypes );

        // exercise
        final GetSchemaTree command = Commands.schema().getTree();
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        assertEquals( 4, command.getResult().deepSize() );
    }

}
