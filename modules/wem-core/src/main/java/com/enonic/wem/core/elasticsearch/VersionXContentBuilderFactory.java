package com.enonic.wem.core.elasticsearch;

import java.time.Instant;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.version.VersionDocument;

public class VersionXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static final String BLOBKEY_FIELD_NAME = "blobKey";

    public static final String ENTITY_ID_FIELD_NAME = "entityId";

    public static final String TIMESTAMP_ID_FIELD_NAME = "timestamp";

    static XContentBuilder create( final VersionDocument versionDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            addField( builder, BLOBKEY_FIELD_NAME, versionDocument.getBlobKey().toString() );
            addField( builder, ENTITY_ID_FIELD_NAME, versionDocument.getEntityId().toString() );
            addField( builder, TIMESTAMP_ID_FIELD_NAME, Instant.now() );

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for WorkspaceDocument", e );
        }

    }

}
