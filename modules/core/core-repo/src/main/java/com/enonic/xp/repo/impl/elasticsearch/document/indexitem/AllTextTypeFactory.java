package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.node.NodeIndexPath;

class AllTextTypeFactory
{
    static List<IndexItem> create( final Value propertyValue, final IndexConfig indexConfig )
    {
        List<IndexItem> allTextItems = Lists.newArrayList();

        if ( indexConfig.isDecideByType() || indexConfig.isIncludeInAllText() )
        {
            allTextItems.add( new IndexItemAnalyzed( NodeIndexPath.ALL_TEXT, propertyValue.asString() ) );

            allTextItems.add( new IndexItemNgram( NodeIndexPath.ALL_TEXT, propertyValue.asString() ) );

        }

        return allTextItems;
    }
}
