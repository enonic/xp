package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;

import static junit.framework.Assert.assertEquals;

public class ContentTypeServiceImpl_getChildrenTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void getChildContentTypes()
        throws Exception
    {
        final ContentType contentType1 = ContentType.
            newContentType().
            name( "mymodule:my_content_type1" ).
            displayName( ContentTypeName.unstructured().toString() ).
            superType( null ).
            setBuiltIn().
            build();

        final ContentType contentType2 = ContentType.
            newContentType().
            name( "mymodule:my_content_type2" ).
            displayName( "Display Name 2" ).
            superType( ContentTypeName.from( "mymodule:my_content_type1" ) ).
            build();

        final ContentType contentType3 = ContentType.
            newContentType().
            name( "mymodule:my_content_type3" ).
            displayName( "Display Name 3" ).
            superType( ContentTypeName.from( "mymodule:my_content_type2" ) ).
            build();

        final ContentType contentType4 = ContentType.
            newContentType().
            name( "mymodule:my_content_type4" ).
            displayName( "Display Name 4" ).
            superType( ContentTypeName.from( "mymodule:my_content_type2" ) ).
            build();

        final ContentType contentType5 = ContentType.
            newContentType().
            name( ContentTypeName.folder().toString() ).
            displayName( "Folder root content type" ).
            setBuiltIn().
            build();

        register( contentType1, contentType2, contentType3, contentType4, contentType5 );

        final GetChildContentTypesParams params1 = new GetChildContentTypesParams().parentName( contentType5.getName() );
        final ContentTypes types1 = this.service.getChildren( params1 );

        assertEquals( 0, types1.getSize() );

        final GetChildContentTypesParams params2 = new GetChildContentTypesParams().parentName( contentType1.getName() );
        final ContentTypes types2 = this.service.getChildren( params2 );

        assertEquals( 1, types2.getSize() );
        assertEquals( "mymodule:my_content_type2", types2.get( 0 ).getName().toString() );

        final GetChildContentTypesParams params3 = new GetChildContentTypesParams().parentName( contentType2.getName() );
        final ContentTypes types3 = this.service.getChildren( params3 );

        assertEquals( 2, types3.getSize() );
        assertEquals( "mymodule:my_content_type3", types3.get( 0 ).getName().toString() );
        assertEquals( "mymodule:my_content_type4", types3.get( 1 ).getName().toString() );
    }
}
