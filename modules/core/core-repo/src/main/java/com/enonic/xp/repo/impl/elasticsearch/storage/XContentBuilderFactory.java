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

    static XContentBuilder create( final IndexDocument doc )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        final String analyzer = doc.analyzer();
        if ( !isNullOrEmpty( analyzer ) )
        {
            // the analyzer is named rather than stored, so it is normalized to the form the index matches by
            addField( builder, IndexConstants.ANALYZER_VALUE_FIELD, IndexValueNormalizer.normalize( analyzer ) );
        }

        for ( final var entry : doc.data().asValuesMap().entrySet() )
        {
            addField( builder, entry.getKey(), entry.getValue() );
        }
        return builder.endObject();
    }

    /**
     * Writes a field as it was given. Values are not normalized here: the document written is what {@code _source} returns, so a value
     * keeps the form it was stored with, and it is the mapping that decides the lowercased token the field is matched by.
     */
    private static void addField( XContentBuilder result, String name, Object value )
        throws IOException
    {
        if ( value == null )
        {
            return;
        }

        result.field( name, value );
    }
}
