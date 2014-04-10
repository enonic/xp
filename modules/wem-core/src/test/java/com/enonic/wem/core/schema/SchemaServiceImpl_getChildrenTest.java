package com.enonic.wem.core.schema;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class SchemaServiceImpl_getChildrenTest
    extends AbstractSchemaServiceImplTest
{
    @Test
    public void getChildSchemas()
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
            superType( unstructuredContentType.getName() ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( this.contentTypeService.getChildren( Mockito.isA( GetChildContentTypesParams.class ) ) ).thenReturn( contentTypes );

        // exercise
        final Schemas schemas = this.schemaService.getChildren( unstructuredContentType.getSchemaKey() );

        // verify
        assertEquals( 1, schemas.getSize() );
        assertTrue( schemas.get( 0 ).getSchemaKey().isContentType() );
        assertEquals( "my_content_type", schemas.get( 0 ).getName().toString() );
    }
}
