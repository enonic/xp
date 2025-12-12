package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.node.NodeIndexPath;

class AllTextTypeFactory
{
    static List<IndexItem> create( final Value propertyValue, final IndexConfig indexConfig, final AllTextIndexConfig allTextIndexConfig )
    {
        final ImmutableList.Builder<IndexItem> allTextItems = ImmutableList.builder();

        if ( allTextIndexConfig.isEnabled() )
        {
            return allTextItems.build();
        }

        if ( ( ( indexConfig.isDecideByType() && propertyValue.isText() ) || indexConfig.isIncludeInAllText() ) )
        {
            if ( allTextIndexConfig.isFulltext() )
            {
                allTextItems.add( new IndexItemAnalyzed( NodeIndexPath.ALL_TEXT, propertyValue.asString() ) );
            }

            if ( allTextIndexConfig.isnGram() )
            {
                allTextItems.add( new IndexItemNgram( NodeIndexPath.ALL_TEXT, propertyValue.asString() ) );
            }

            allTextIndexConfig.getLanguages()
                .stream()
                .map( language -> new IndexItemStemmed( NodeIndexPath.ALL_TEXT, propertyValue.asString(), language ) )
                .filter( indexItem -> indexItem.valueType() != null )
                .forEach( allTextItems::add );
        }

        return allTextItems.build();
    }
}
