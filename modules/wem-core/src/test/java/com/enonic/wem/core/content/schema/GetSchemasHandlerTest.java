package com.enonic.wem.core.content.schema;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.SchemaTypes;
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

public class GetSchemasHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetSchemasHandler handler;

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
        handler = new GetSchemasHandler();
        handler.setContentTypeDao( contentTypeDao );
        handler.setMixinDao( mixinDao );
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void getSchemas()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            setAbstract( false ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( contentTypeDao.selectAll( any( Session.class ) ) ).thenReturn( contentTypes );

        final FormItemSet formItemSet = newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build();
        final Mixin mixin = newMixin().
            module( ModuleName.from( "myModule" ) ).
            formItem( formItemSet ).
            build();
        final Mixins mixinTypes = Mixins.from( mixin );
        Mockito.when( mixinDao.selectAll( any( Session.class ) ) ).thenReturn( mixinTypes );

        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( relationshipTypeDao.selectAll( any( Session.class ) ) ).thenReturn( relationshipTypes );

        // exercise
        final SchemaTypes command = Commands.schema().get();
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        verify( mixinDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        verify( relationshipTypeDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        assertEquals( 3, command.getResult().getSize() );
    }

}
