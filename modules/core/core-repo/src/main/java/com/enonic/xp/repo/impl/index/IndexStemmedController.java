package com.enonic.xp.repo.impl.index;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;

@Component
public class IndexStemmedController
{
    private static Map<String, String> SUPPORTED_ANALYZERS;

    private static Map<String, StemmedIndexValueType> SUPPORTED_INDEX_VALUE_TYPES;

    private IndexService indexService;

    public static String resolveAnalyzer( final String language )
    {
        if ( SUPPORTED_ANALYZERS.keySet().contains( language ) )
        {
            return SUPPORTED_ANALYZERS.get( language );
        }

        return null;
    }

    public static IndexValueTypeInterface resolveIndexValueType( final String language )
    {
        if ( SUPPORTED_INDEX_VALUE_TYPES.keySet().contains( language ) )
        {
            return SUPPORTED_INDEX_VALUE_TYPES.get( language );
        }

        return null;
    }

    @Activate
    private void init()
    {
        final Map<String, Object> indexMapping =
            indexService.getIndexMapping( ContextAccessor.current().getRepositoryId(), ContextAccessor.current().getBranch(),
                                          IndexType.SEARCH );

        final IndexMetaDataParser parser = new IndexMetaDataParser( indexMapping );
        parser.parse();

        SUPPORTED_ANALYZERS = parser.getStemmedAnalyzers();
        SUPPORTED_INDEX_VALUE_TYPES = parser.getStemmedIndexValueTypes();
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

}
