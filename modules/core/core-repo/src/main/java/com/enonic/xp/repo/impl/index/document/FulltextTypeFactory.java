package com.enonic.xp.repo.impl.index.document;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;

public class FulltextTypeFactory
{
    public static List<IndexItem> create( final String key, final Value value, final IndexConfig indexConfig )
    {
        List<IndexItem> fulltextItems = Lists.newArrayList();

        if ( indexConfig.isDecideByType() )
        {
            if ( value.isText() )
            {
                fulltextItems.add( new IndexItemAnalyzed( key, value.asString() ) );
                fulltextItems.add( new IndexItemNgram( key, value.asString() ) );
            }
        }
        else
        {
            if ( indexConfig.isFulltext() )
            {
                fulltextItems.add( new IndexItemAnalyzed( key, value.asString() ) );
            }

            if ( indexConfig.isnGram() )
            {
                fulltextItems.add( new IndexItemNgram( key, value.asString() ) );
            }
        }

        return fulltextItems;
    }
}
