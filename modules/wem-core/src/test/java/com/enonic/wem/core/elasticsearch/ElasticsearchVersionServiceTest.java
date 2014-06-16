package com.enonic.wem.core.elasticsearch;

import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntries;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionBranchQuery;
import com.enonic.wem.core.version.VersionDocument;
import com.enonic.wem.core.version.VersionEntry;

import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.BLOBKEY_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.PARENT_ID_FIELD_NAME;

public class ElasticsearchVersionServiceTest
{
    private final ElasticsearchDao elasticsearchDao = Mockito.mock( ElasticsearchDao.class );

    private final ElasticsearchVersionService versionService = new ElasticsearchVersionService();

    @Before
    public void setUp()
        throws Exception
    {
        versionService.setElasticsearchDao( elasticsearchDao );
    }

    @Test
    public void getBranch_no_parent()
        throws Exception
    {
        final VersionBranchQuery versionBranchQuery = new VersionBranchQuery( new BlobKey( "1" ) );

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( TermQueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        build() ).
                    build() ).
                build() );

        final VersionBranch branch = versionService.getBranch( versionBranchQuery );

        Assert.assertEquals( 1, branch.size() );

        final Iterator<VersionEntry> iterator = branch.iterator();

        Assert.assertEquals( new BlobKey( "1" ), iterator.next().getBlobKey() );
    }

    @Test
    public void getBranch_one_parent()
        throws Exception
    {
        final VersionBranchQuery versionBranchQuery = new VersionBranchQuery( new BlobKey( "1.1" ) );

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( TermQueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        addField( PARENT_ID_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "1" ) ).
                        build() ).
                    build() ).
                build() ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        build() ).
                    build() ).
                build() );

        final VersionBranch branch = versionService.getBranch( versionBranchQuery );

        Assert.assertEquals( 2, branch.size() );

        final Iterator<VersionEntry> iterator = branch.iterator();

        Assert.assertEquals( new BlobKey( "1.1" ), iterator.next().getBlobKey() );
        Assert.assertEquals( new BlobKey( "1" ), iterator.next().getBlobKey() );
    }


    @Test
    public void getBranch_several_parents()
        throws Exception
    {
        final VersionBranchQuery versionBranchQuery = new VersionBranchQuery( new BlobKey( "1.1.1.1" ) );

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( TermQueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        addField( PARENT_ID_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "1.1.1" ) ).
                        build() ).
                    build() ).
                build() ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        addField( PARENT_ID_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "1.1" ) ).
                        build() ).
                    build() ).
                build() ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        addField( PARENT_ID_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "1" ) ).
                        build() ).
                    build() ).
                build() ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        build() ).
                    build() ).
                build() );

        final VersionBranch branch = versionService.getBranch( versionBranchQuery );

        Assert.assertEquals( 4, branch.size() );

        final Iterator<VersionEntry> iterator = branch.iterator();

        Assert.assertEquals( new BlobKey( "1.1.1.1" ), iterator.next().getBlobKey() );
        Assert.assertEquals( new BlobKey( "1.1.1" ), iterator.next().getBlobKey() );
        Assert.assertEquals( new BlobKey( "1.1" ), iterator.next().getBlobKey() );
        Assert.assertEquals( new BlobKey( "1" ), iterator.next().getBlobKey() );
    }

    @Test
    public void store()
        throws Exception
    {
        versionService.store( VersionDocument.create().
            blobKey( new BlobKey( "1" ) ).
            entityId( EntityId.from( "a" ) ).
            build() );

        Mockito.verify( elasticsearchDao, Mockito.times( 1 ) ).store( Mockito.isA( IndexRequest.class ) );
    }
}



