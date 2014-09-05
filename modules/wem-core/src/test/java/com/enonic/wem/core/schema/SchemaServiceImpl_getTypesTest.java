package com.enonic.wem.core.schema;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.SchemaTypesParams;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class SchemaServiceImpl_getTypesTest
    extends AbstractSchemaServiceImplTest
{
    @Test
    public void getSchemas()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            name( "mymodule-1.0.0:my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType );

        Mockito.when( this.contentTypeService.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn( contentTypes );

        final FormItemSet formItemSet = newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build();
        final Mixin mixin = newMixin().name( "mymodule-1.0.0:address" ).
            addFormItem( formItemSet ).
            build();
        final Mixins mixins = Mixins.from( mixin );
        Mockito.when( this.mixinService.getAll() ).thenReturn( mixins );

        final RelationshipType relationshipType = newRelationshipType().
            name( "mymodule-1.0.0:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( this.relationshipTypeService.getAll() ).thenReturn( relationshipTypes );

        // exercise
        final Schemas schemas = this.schemaService.getTypes( new SchemaTypesParams() );

        // verify
        assertEquals( 3, schemas.getSize() );
    }
}
