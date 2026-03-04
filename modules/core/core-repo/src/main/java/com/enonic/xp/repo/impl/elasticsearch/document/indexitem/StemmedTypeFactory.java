package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexLanguageController;

class StemmedTypeFactory
{
    public static List<IndexItem> create( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        final ImmutableList.Builder<IndexItem> stemmedItems = ImmutableList.builder();

        if ( indexConfig.isStemmed() )
        {
            indexConfig.getLanguages().forEach( language -> {
                if ( !IndexLanguageController.isSupported( language ) )
                {
                    throw new IllegalArgumentException( "Unsupported language for stemmed indexing: " + language );
                }
                stemmedItems.add( new IndexItemStemmed( indexPath, value.asString(), language ) );
            } );
        }

        return stemmedItems.build();
    }
}
