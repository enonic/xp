package com.enonic.wem.core.index.content;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.indexdocument.IndexDocument;
import com.enonic.wem.core.index.indexdocument.IndexDocumentEntry;

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
        assertEquals( 7, indexDocumentEntries.size() );
    }


    @Test
    public void testContentData()
        throws Exception
    {
        Content content = createContentWithMetadata();

        content.getContentData().setProperty( "mydata.value1", new Value.Double( 1.0 ) );
        content.getContentData().setProperty( "mydata.value2", new Value.DateMidnight( org.joda.time.DateMidnight.now() ) );
        content.getContentData().setProperty( "mydata.value2[1]", new Value.DateMidnight( org.joda.time.DateMidnight.now() ) );
        content.getContentData().setProperty( "mydata.value3.subvalue1", new Value.String( "value3.1" ) );
        content.getContentData().setProperty( "mydata.value3.subvalue2", new Value.String( "value3.2" ) );
        content.getContentData().setProperty( "mydata.value3.subvalue3", new Value.String( "value3.3" ) );

        final Collection<IndexDocument> indexDocuments = ContentIndexDocumentsFactory.create( content );

        assertEquals( 1, indexDocuments.size() );
        final IndexDocument indexDocument = indexDocuments.iterator().next();

        final Set<IndexDocumentEntry> indexDocumentEntries = indexDocument.getIndexDocumentEntries();

        // Key, path, type, owner, modifier, created, lastModified, displayName + 6 content data values
        assertEquals( 13, indexDocumentEntries.size() );
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
            id( new ContentId( "eplekake" ) ).
            build();
    }


}




