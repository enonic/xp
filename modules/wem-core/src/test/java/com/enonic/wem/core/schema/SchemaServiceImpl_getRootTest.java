package com.enonic.wem.core.schema;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static junit.framework.Assert.assertEquals;

public class SchemaServiceImpl_getRootTest
    extends AbstractSchemaServiceImplTest
{
    @Test
    public void getRootSchemas()
        throws Exception
    {
        // setup
        final ContentType unstructuredContentType = newContentType().
            name( ContentTypeName.structured() ).
            setBuiltIn().
            displayName( "Unstructured" ).
            setFinal( false ).
            setAbstract( false ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( unstructuredContentType );
        Mockito.when( this.contentTypeService.getRoots() ).thenReturn( contentTypes );

        final FormItemSet formItemSet = newFormItemSet().
            name( "address" ).
            addFormItem( newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).
            build();

        final Mixin mixin = newMixin().name( "address" ).
            addFormItem( formItemSet ).
            build();

        final Mixins mixins = Mixins.from( mixin );
        Mockito.when( this.mixinService.getAll() ).thenReturn( mixins );

        final RelationshipType relationshipType = newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( this.relationshipTypeService.getAll() ).thenReturn( relationshipTypes );

        // exercise
        final Schemas schemas = this.schemaService.getRoot();

        // verify
        assertEquals( 3, schemas.getSize() );
    }
}
