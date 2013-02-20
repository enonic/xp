package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetBaseTypes;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.Mixins;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.mixin.dao.MixinDao;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.mixin.Mixin.newMixin;
import static com.enonic.wem.api.content.schema.relationshiptype.RelationshipType.newRelationshipType;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GetBaseTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetBaseTypesHandler handler;

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
        handler = new GetBaseTypesHandler();
        handler.setContentTypeDao( contentTypeDao );
        handler.setMixinDao( mixinDao );
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void getBaseTypes()
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

        final FormItemSet formItemSet =
            newFormItemSet().name( "address" ).add( newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build();
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
        final GetBaseTypes command = Commands.baseType().get();
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        verify( mixinDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        verify( relationshipTypeDao, times( 1 ) ).selectAll( Mockito.any( Session.class ) );
        assertEquals( 3, command.getResult().getSize() );
    }

}
