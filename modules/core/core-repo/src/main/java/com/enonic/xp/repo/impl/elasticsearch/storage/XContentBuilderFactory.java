package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.xp.repo.impl.elasticsearch.IndexConstants;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;
import com.enonic.xp.repo.impl.storage.StoreRequest;

import static com.google.common.base.Strings.isNullOrEmpty;

class XContentBuilderFactory
{
    static XContentBuilder create( final StoreRequest doc )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        for ( final var e : doc.getData().asValuesMap().entrySet() )
        {
            addField( builder, e.getKey(), e.getValue() );
        }
        return builder.endObject();
    }

    static XContentBuilder create( final IndexDocument indexDocument )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        final String analyzer = indexDocument.getAnalyzer();
        if ( !isNullOrEmpty( analyzer ) )
        {
            addField( builder, IndexConstants.ANALYZER_VALUE_FIELD, analyzer );
        }

        for ( final var entry : indexDocument.getIndexItems().asValuesMap().entrySet() )
        {
            addField( builder, entry.getKey(), entry.getValue() );
        }
        return builder.endObject();
    }

    private static void addField( XContentBuilder result, String name, Object value )
        throws IOException
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof String )
        {
            value = IndexValueNormalizer.normalize( (String) value );
        }

        result.field( name, value );
    }
}
