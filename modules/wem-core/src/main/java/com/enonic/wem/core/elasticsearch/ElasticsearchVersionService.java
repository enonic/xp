package com.enonic.wem.core.elasticsearch;

import java.util.Set;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionBranchQuery;
import com.enonic.wem.core.version.VersionDocument;
import com.enonic.wem.core.version.VersionEntry;
import com.enonic.wem.core.version.VersionNotFoundException;
import com.enonic.wem.core.version.VersionService;

import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.BLOBKEY_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.PARENT_ID_FIELD_NAME;

public class ElasticsearchVersionService
    implements VersionService
{
    private final static Index VERSION_INDEX = Index.VERSION;

    private static final boolean DEFAULT_REFRESH = true;

    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final VersionDocument versionDocument )
    {
        final IndexRequest versionsDocument = Requests.indexRequest().
            index( VERSION_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            source( VersionXContentBuilderFactory.create( versionDocument ) ).
            id( versionDocument.getId().toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( versionsDocument );
    }

    @Override
    public VersionBranch getBranch( final VersionBranchQuery query )
    {
        return VersionBranch.create().set( doGetEntries( query.getBlobKey() ) ).build();
    }

    private Set<VersionEntry> doGetEntries( final BlobKey blobKey )
    {
        final TermQueryBuilder blobKeyQuery = new TermQueryBuilder( BLOBKEY_FIELD_NAME, blobKey.toString() );

        final QueryMetaData queryMetaData = QueryMetaData.create( VERSION_INDEX ).
            addFields( PARENT_ID_FIELD_NAME ).
            size( 1 ).
            from( 0 ).
            indexType( IndexType.NODE ).
            build();

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, blobKeyQuery );

        if ( searchResult.getResults().getSize() == 0 )
        {
            throw new VersionNotFoundException( "Could not find version with blobKey: " + blobKey );
        }

        return createVersionEntriesFromSearchResult( blobKey, searchResult );
    }

    private Set<VersionEntry> createVersionEntriesFromSearchResult( final BlobKey blobKey, final SearchResult searchResult )
    {
        final Set<VersionEntry> versionEntries = Sets.newLinkedHashSet();

        final SearchResultEntry hit = searchResult.getResults().getFirstHit();

        final String parentKey = getStringValue( hit, PARENT_ID_FIELD_NAME, false );

        versionEntries.add( new VersionEntry( blobKey, Strings.isNullOrEmpty( parentKey ) ? null : new BlobKey( parentKey ) ) );

        if ( !Strings.isNullOrEmpty( parentKey ) )
        {
            versionEntries.addAll( doGetEntries( new BlobKey( parentKey ) ) );
        }

        return versionEntries;
    }

    private String getStringValue( final SearchResultEntry hit, final String fieldName, final boolean required )
    {
        final SearchResultField field = hit.getField( fieldName, required );

        if ( field == null )
        {
            return null;
        }

        return field.getValue().toString();
    }


    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
