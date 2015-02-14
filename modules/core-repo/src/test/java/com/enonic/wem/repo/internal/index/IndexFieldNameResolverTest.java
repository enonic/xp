package com.enonic.wem.repo.internal.index;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.index.IndexPath;
import com.enonic.wem.repo.internal.elasticsearch.FieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentAnalyzedItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentDateItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentGeoPointItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentNGramItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentNumberItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentStringItem;

public class IndexFieldNameResolverTest
{
    @Test
    public void resolveNameForItemType()
    {
        resolveNameForItemType( new StoreDocumentStringItem( IndexPath.from( "a.b.c" ), "myString" ), "a_b_c" );
        resolveNameForItemType( new StoreDocumentNumberItem( IndexPath.from( "a.b.c" ), 1d ), "a_b_c._number" );
        resolveNameForItemType( new StoreDocumentAnalyzedItem( IndexPath.from( "a.b.c" ), "myString" ), "a_b_c._analyzed" );
        resolveNameForItemType( new StoreDocumentNGramItem( IndexPath.from( "a.b.c" ), "myString" ), "a_b_c._ngram" );
        resolveNameForItemType( new StoreDocumentGeoPointItem( IndexPath.from( "a.b.c" ), "80,80" ), "a_b_c._geopoint" );
        resolveNameForItemType( new StoreDocumentOrderbyItem( IndexPath.from( "a.b.c" ), "orderBy" ), "a_b_c._orderby" );
        resolveNameForItemType( new StoreDocumentDateItem( IndexPath.from( "a.b.c" ), Instant.now() ), "a_b_c._datetime" );
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
        resolveNameFromPath( "a.b", "a_b" );
        resolveNameFromPath( "a.b.c", "a_b_c" );
        resolveNameFromPath( "a.b.c.d", "a_b_c_d" );
    }

    private void resolveNameFromPath( final String pathAsString, final String expected )
    {
        final String result = FieldNameResolver.resolve( new StoreDocumentStringItem( IndexPath.from( pathAsString ), "myValue" ) );
        Assert.assertEquals( expected, result );
    }
}
