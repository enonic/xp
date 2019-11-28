package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;

class StemmedTypeFactory
{
    public static List<IndexItem> create( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        List<IndexItem> stemmedItems = new ArrayList<>();

        if ( indexConfig.isStemmed() )
        {
            indexConfig.getLanguages().
                forEach( language -> stemmedItems.add( new IndexItemStemmed( indexPath, value.asString(), language ) ) );
        }

        return stemmedItems;
    }
}
