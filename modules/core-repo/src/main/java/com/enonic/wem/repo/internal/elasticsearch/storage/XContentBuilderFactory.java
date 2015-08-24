package com.enonic.wem.repo.internal.elasticsearch.storage;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.index.IndexValueNormalizer;
import com.enonic.wem.repo.internal.storage.StorageDataEntry;
import com.enonic.wem.repo.internal.storage.StoreRequest;

public class XContentBuilderFactory

{
    public static XContentBuilder create( final StoreRequest doc )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            final Set<StorageDataEntry> entries = doc.getEntries();

            for ( final StorageDataEntry entry : entries )
            {
                addField( builder, entry.getKey(), entry.getValue() );
            }

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for StorageDocument", e );
        }
    }

    private static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }

    private static void addField( XContentBuilder result, String name, Object value )
        throws Exception
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


    private static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }


}
