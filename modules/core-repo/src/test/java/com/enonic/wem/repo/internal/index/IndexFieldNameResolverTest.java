package com.enonic.wem.repo.internal.index;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.repo.internal.elasticsearch.FieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentAnalyzedItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentDateItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentGeoPointItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentNGramItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentNumberItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentStringItem;
import com.enonic.xp.index.IndexPath;

public class IndexFieldNameResolverTest
{
    @Test
    public void resolveNameForItemType()
    {
        resolveNameForItemType( new StoreDocumentStringItem( IndexPath.from( "a.b.c" ), "myString" ), "a.b.c" );
        resolveNameForItemType( new StoreDocumentNumberItem( IndexPath.from( "a.b.c" ), 1d ), "a.b.c._number" );
        resolveNameForItemType( new StoreDocumentAnalyzedItem( IndexPath.from( "a.b.c" ), "myString" ), "a.b.c._analyzed" );
        resolveNameForItemType( new StoreDocumentNGramItem( IndexPath.from( "a.b.c" ), "myString" ), "a.b.c._ngram" );
        resolveNameForItemType( new StoreDocumentGeoPointItem( IndexPath.from( "a.b.c" ), "80,80" ), "a.b.c._geopoint" );
        resolveNameForItemType( new StoreDocumentOrderbyItem( IndexPath.from( "a.b.c" ), "orderBy" ), "a.b.c._orderby" );
        resolveNameForItemType( new StoreDocumentDateItem( IndexPath.from( "a.b.c" ), Instant.now() ), "a.b.c._datetime" );
    }

    private void resolveNameForItemType( final AbstractStoreDocumentItem item, final String expected )
    {
        final String result = FieldNameResolver.resolve( item );
        Assert.assertEquals( expected, result );
    }

    @Test
    public void resolveNameFromPath()
    {
        resolveNameFromPath( "a", "a" );
        resolveNameFromPath( "a.b", "a.b" );
        resolveNameFromPath( "a.b.c", "a.b.c" );
        resolveNameFromPath( "a.b.c.d", "a.b.c.d" );
    }

    private void resolveNameFromPath( final String pathAsString, final String expected )
    {
        final String result = FieldNameResolver.resolve( new StoreDocumentStringItem( IndexPath.from( pathAsString ), "myValue" ) );
        Assert.assertEquals( expected, result );
    }
}
