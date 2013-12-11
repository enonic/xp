package com.enonic.wem.core.schema.content;


import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.content.blob.CreateBlob;
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
        Mockito.when( client.execute( Mockito.isA( CreateBlob.class ) ) ).thenReturn( createDummyBlob() );
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

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

    private Blob createDummyBlob()
    {
        return new Blob()
        {
            @Override
            public BlobKey getKey()
            {
                return new BlobKey( "ABC" );
            }

            @Override
            public long getLength()
            {
                return 0;
            }

            @Override
            public InputStream getStream()
            {
                return null;
            }
        };
    }
}
