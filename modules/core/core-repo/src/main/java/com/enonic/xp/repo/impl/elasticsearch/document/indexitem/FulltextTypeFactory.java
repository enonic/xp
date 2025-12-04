package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;

class FulltextTypeFactory
{
    public static List<IndexItem> create( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        final ImmutableList.Builder<IndexItem> fulltextItems = ImmutableList.builder();

        if ( indexConfig.isDecideByType() )
        {
            if ( value.isText() )
            {
                fulltextItems.add( new IndexItemAnalyzed( indexPath, value.asString() ) );
                fulltextItems.add( new IndexItemNgram( indexPath, value.asString() ) );
            }
        }
        else
        {
            if ( indexConfig.isFulltext() )
            {
                fulltextItems.add( new IndexItemAnalyzed( indexPath, value.asString() ) );
            }

            if ( indexConfig.isnGram() )
            {
                fulltextItems.add( new IndexItemNgram( indexPath, value.asString() ) );
            }
        }

        return fulltextItems.build();
    }
}
