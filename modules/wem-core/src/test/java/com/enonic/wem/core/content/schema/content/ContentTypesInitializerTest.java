package com.enonic.wem.core.content.schema.content;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.schema.content.ContentTypes;

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

        assertEquals( true, ContentTypesInitializer.SPACE.isFinal() );
        assertEquals( false, ContentTypesInitializer.SPACE.isAbstract() );

        assertEquals( false, ContentTypesInitializer.STRUCTURED.isFinal() );
        assertEquals( true, ContentTypesInitializer.STRUCTURED.isAbstract() );

        assertEquals( false, ContentTypesInitializer.UNSTRUCTURED.isFinal() );
        assertEquals( false, ContentTypesInitializer.UNSTRUCTURED.isAbstract() );

        assertEquals( false, ContentTypesInitializer.FILE.isFinal() );
        assertEquals( false, ContentTypesInitializer.FILE.isAbstract() );

        assertEquals( true, ContentTypesInitializer.FILE_TEXT.isFinal() );
        assertEquals( false, ContentTypesInitializer.FILE_TEXT.isAbstract() );
    }
}
