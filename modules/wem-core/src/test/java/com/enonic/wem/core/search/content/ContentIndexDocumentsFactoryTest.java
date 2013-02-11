package com.enonic.wem.core.search.content;

import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

import static org.junit.Assert.*;

public class ContentIndexDocumentsFactoryTest
{

    @Test
    public void testCreateContentIndexDocument()
        throws Exception
    {

        final UserKey me = UserKey.from( "system:rmy" );
        Content content = Content.newContent().
            name( "content" ).
            modifier( me ).owner( me ).
            path( ContentPath.from( "/wem/content/test" ) ).
            modifiedTime( DateTime.now() ).
            displayName( "Displayname" ).
            id( new MockContentId( "eplekake" ) ).
            build();

        final Collection<IndexDocument> indexDocuments = ContentIndexDocumentsFactory.create( content );

        assertEquals( 1, indexDocuments.size() );


    }

    private final class MockContentId
        implements ContentId
    {
        final String id;

        private MockContentId( final String id )
        {
            this.id = id;
        }
    }


}




