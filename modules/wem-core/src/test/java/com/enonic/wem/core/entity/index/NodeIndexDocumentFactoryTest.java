package com.enonic.wem.core.entity.index;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument2;
import com.enonic.wem.core.index.document.IndexValueType;

import static org.junit.Assert.*;

public class NodeIndexDocumentFactoryTest
{
    private NodeIndexDocumentFactory factory = new NodeIndexDocumentFactory();

    @Ignore // Create better test you idiot
    @Test
    public void index_document_meta_data()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue" ) );
        rootDataSet.setProperty( "a/b", new Value.Double( 2.0 ) );
        rootDataSet.setProperty( "a/b/c", new Value.DateMidnight( DateMidnight.now() ) );

        final String myAnalyzerName = "myAnalyzer";

        DateTime modifiedDateTime = new DateTime( 2013, 01, 02, 03, 04, 05 );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            parent( NodePath.ROOT ).
            createdTime( DateTime.now() ).
            name( "myName" ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ).asUser() ).
            modifiedTime( modifiedDateTime ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().
                analyzer( myAnalyzerName ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocument = factory.create( node );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstAndOnlyDoc = indexDocument.iterator().next();

        assertEquals( myAnalyzerName, firstAndOnlyDoc.getAnalyzer() );
        assertEquals( IndexConstants.NODB_INDEX, firstAndOnlyDoc.getIndex() );
        assertEquals( IndexType.NODE, firstAndOnlyDoc.getIndexType() );

        final AbstractIndexDocumentItem createdTimeItem =
            firstAndOnlyDoc.getItemWithName( NodeIndexDocumentFactory.CREATED_TIME_PROPERTY_NAME, IndexValueType.DATETIME );

        assertEquals( node.getCreatedTime(), createdTimeItem.getValue() );

        final AbstractIndexDocumentItem creator =
            firstAndOnlyDoc.getItemWithName( NodeIndexDocumentFactory.CREATOR_PROPERTY_NAME, IndexValueType.STRING );

        assertEquals( "test:creator", creator.getValue() );

        final AbstractIndexDocumentItem modifier =
            firstAndOnlyDoc.getItemWithName( NodeIndexDocumentFactory.MODIFIER_PROPERTY_NAME, IndexValueType.STRING );

        assertEquals( "test:modifier", modifier.getValue() );

    }

    @Ignore // Create better test you idiot
    @Test
    public void string_array_value_document()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue1" ) );
        rootDataSet.addProperty( "a", new Value.String( "myValue2" ) );
        rootDataSet.addProperty( "a", new Value.String( "myValue3" ) );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            parent( NodePath.ROOT ).
            createdTime( DateTime.now() ).
            name( "myName" ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().
                analyzer( "default" ).
                addPropertyIndexConfig( "a", PropertyIndexConfig.
                    newPropertyIndexConfig().
                    enabled( true ).
                    autocompleteEnabled( true ).
                    fulltextEnabled( true ).
                    build() ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocument = factory.create( node );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstAndOnlyDocument = indexDocument.iterator().next();

        final Set<AbstractIndexDocumentItem> indexDocumentEntries = firstAndOnlyDocument.getIndexDocumentItems();

        // All three values should have an entry for: string, analyzed, tokenized, orderby, thus 12 elements, plus the number of meta-data
        assertEquals( 12 + 5, indexDocumentEntries.size() );
    }

    @Ignore // Create better test you idiot
    @Test
    public void create_document_without_property_index_settings()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue" ) );
        rootDataSet.setProperty( "a/b", new Value.Double( 2.0 ) );
        rootDataSet.addProperty( "a/b", new Value.Double( 3.0 ) );
        rootDataSet.setProperty( "a/b/c", new Value.DateMidnight( DateMidnight.now() ) );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            parent( NodePath.ROOT ).
            createdTime( DateTime.now() ).
            name( "myName" ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().
                analyzer( "default" ).
                build() ).
            build();

        NodeIndexDocumentFactory factory = new NodeIndexDocumentFactory();

        final Collection<IndexDocument2> indexDocument = factory.create( node );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstDoc = indexDocument.iterator().next();

    }


}
