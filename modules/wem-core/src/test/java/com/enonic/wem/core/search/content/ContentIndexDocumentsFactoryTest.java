package com.enonic.wem.core.search.content;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.search.indexdocument.IndexDocument;
import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;

import static org.junit.Assert.*;

public class ContentIndexDocumentsFactoryTest
{

    @Test
    public void testMetadata()
        throws Exception
    {

        Content content = createContentWithMetadata();

        final Collection<IndexDocument> indexDocuments = ContentIndexDocumentsFactory.create( content );

        assertEquals( 1, indexDocuments.size() );
        final IndexDocument indexDocument = indexDocuments.iterator().next();

        final Set<IndexDocumentEntry> indexDocumentEntries = indexDocument.getIndexDocumentEntries();

        // Key, path, type, owner, modifier, created, lastModified, displayName
        assertEquals( 8, indexDocumentEntries.size() );
    }


    @Test
    public void testContentData()
        throws Exception
    {
        Content content = createContentWithMetadata();

        content.setData( "mydata.value1", 1.0 );
        content.setData( "mydata.value2", DateMidnight.now() );
        content.setData( "mydata.value2[1]", DateMidnight.now() );
        content.setData( "mydata.value3.subvalue1", "value3.1" );
        content.setData( "mydata.value3.subvalue2", "value3.2" );
        content.setData( "mydata.value3.subvalue3", "value3.3" );

        final Collection<IndexDocument> indexDocuments = ContentIndexDocumentsFactory.create( content );

        assertEquals( 1, indexDocuments.size() );
        final IndexDocument indexDocument = indexDocuments.iterator().next();

        final Set<IndexDocumentEntry> indexDocumentEntries = indexDocument.getIndexDocumentEntries();

        // Key, path, type, owner, modifier, created, lastModified, displayName + 6 content data values
        assertEquals( 14, indexDocumentEntries.size() );
    }


    private Content createContentWithMetadata()
    {
        final UserKey me = UserKey.from( "system:rmy" );
        return Content.newContent().
            name( "content" ).
            modifier( me ).
            owner( me ).
            path( ContentPath.from( "/wem/content/test" ) ).
            modifiedTime( DateTime.now() ).
            displayName( "Displayname" ).
            id( new MockContentId( "eplekake" ) ).
            build();
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




