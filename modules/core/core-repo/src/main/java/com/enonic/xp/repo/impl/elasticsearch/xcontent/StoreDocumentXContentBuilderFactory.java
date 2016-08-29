package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import java.util.stream.Collectors;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.repo.impl.elasticsearch.IndexConstants;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexValue;
import com.enonic.xp.repository.IndexException;

public class StoreDocumentXContentBuilderFactory
    extends AbstractXContentBuilderFactory
{

    private StoreDocumentXContentBuilderFactory()
    {
    }

    public static XContentBuilder create( final IndexDocument indexDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addDocumentAnalyzer( builder, indexDocument );
            addIndexDocumentItems( builder, indexDocument );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource", e );
        }
    }


    private static void addDocumentAnalyzer( final XContentBuilder builder, final IndexDocument indexDocument )
        throws Exception
    {
        final String analyzer = indexDocument.getAnalyzer();

        if ( !Strings.isNullOrEmpty( analyzer ) )
        {
            addField( builder, IndexConstants.ANALYZER_VALUE_FIELD, analyzer );
        }
    }

    private static void addIndexDocumentItems( final XContentBuilder result, final IndexDocument indexDocument )
        throws Exception
    {
        final IndexItems indexItems = indexDocument.getIndexItems();

        for ( final String key : indexItems )
        {
            addField( result, key, indexItems.get( key ).stream().
                map( IndexValue::getValue ).
                collect( Collectors.toList() ) );
        }
    }

}
