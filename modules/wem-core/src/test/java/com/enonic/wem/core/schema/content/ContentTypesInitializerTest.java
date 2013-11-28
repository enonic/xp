package com.enonic.wem.core.schema.content;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;

import static junit.framework.Assert.assertEquals;

public class ContentTypesInitializerTest
{
    @Test
    public void system()
        throws Exception
    {
        Client client = Mockito.mock( Client.class );
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        ContentTypesInitializer contentTypesInitializer = new ContentTypesInitializer();
        contentTypesInitializer.setClient( client );
        contentTypesInitializer.initialize();

        assertEquals( false, ContentTypesInitializer.STRUCTURED.isFinal() );
        assertEquals( true, ContentTypesInitializer.STRUCTURED.isAbstract() );
        assertEquals( null, ContentTypesInitializer.STRUCTURED.getSuperType() );

        assertEquals( false, ContentTypesInitializer.UNSTRUCTURED.isFinal() );
        assertEquals( false, ContentTypesInitializer.UNSTRUCTURED.isAbstract() );
        assertEquals( null, ContentTypesInitializer.UNSTRUCTURED.getSuperType() );

        assertEquals( false, ContentTypesInitializer.MEDIA.isFinal() );
        assertEquals( false, ContentTypesInitializer.MEDIA.isAbstract() );
        assertEquals( null, ContentTypesInitializer.MEDIA.getSuperType() );

        assertEquals( true, ContentTypesInitializer.MEDIA_TEXT.isFinal() );
        assertEquals( false, ContentTypesInitializer.MEDIA_TEXT.isAbstract() );
        assertEquals( ContentTypeName.media(), ContentTypesInitializer.MEDIA_TEXT.getSuperType() );
    }
}
