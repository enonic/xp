package com.enonic.wem.core.content.schema;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.GetSchemaTree;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.content.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GetSchemaTreeHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetSchemaTreeHandler handler;

    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        mixinDao = Mockito.mock( MixinDao.class );
        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new GetSchemaTreeHandler();
        handler.setContentTypeDao( contentTypeDao );
        handler.setMixinDao( mixinDao );
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void getSchemaTree()
        throws Exception
    {
        // setup
        final ContentType unstructuredContentType = newContentType().
            qualifiedName( QualifiedContentTypeName.structured() ).
            displayName( "Unstructured" ).
            setFinal( false ).
            setAbstract( false ).
            build();

        final ContentType contentType = newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( unstructuredContentType.getQualifiedName() ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType, unstructuredContentType );
        Mockito.when( contentTypeDao.selectAll( any( Session.class ) ) ).thenReturn( contentTypes );

        final FormItemSet formItemSet = newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build();
        final Mixin mixin = newMixin().
            module( ModuleName.from( "mymodule" ) ).
            formItem( formItemSet ).
            build();
        final Mixins mixinTypes = Mixins.from( mixin );
        Mockito.when( mixinDao.selectAll( any( Session.class ) ) ).thenReturn( mixinTypes );

        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( relationshipTypeDao.selectAll( any( Session.class ) ) ).thenReturn( relationshipTypes );

        // exercise
        final GetSchemaTree command = Commands.schema().getTree();
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        verify( mixinDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        verify( relationshipTypeDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        assertEquals( 4, command.getResult().deepSize() );
    }

}
