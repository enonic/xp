package com.enonic.xp.repo.impl.index;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;

public class IndexStemmedController
{
    private static Map<String, String> SUPPORTED_ANALYZERS;

    private static Map<String, StemmedIndexValueType> SUPPORTED_INDEX_VALUE_TYPES;

    static
    {
        final Bundle bundle = FrameworkUtil.getBundle( IndexService.class );

        if ( bundle != null )
        {
            final BundleContext bundleContext = bundle.getBundleContext();
            final IndexService indexService = bundleContext.getService( bundleContext.getServiceReference( IndexService.class ) );

            final Map<String, Object> indexMapping =
                indexService.getIndexMapping( ContextAccessor.current().getRepositoryId(), ContextAccessor.current().getBranch(),
                                              IndexType.SEARCH );

            final IndexMetaDataParser parser = new IndexMetaDataParser( indexMapping );
            parser.parse();

            IndexStemmedController.SUPPORTED_ANALYZERS = parser.getStemmedAnalyzers();
            IndexStemmedController.SUPPORTED_INDEX_VALUE_TYPES = parser.getStemmedIndexValueTypes();
        }
    }

    public static String resolveAnalyzer( final String language )
    {
        if ( SUPPORTED_ANALYZERS != null && SUPPORTED_ANALYZERS.keySet().contains( language ) )
        {
            return SUPPORTED_ANALYZERS.get( language );
        }

        return null;
    }

    public static IndexValueTypeInterface resolveIndexValueType( final String language )
    {
        if ( SUPPORTED_INDEX_VALUE_TYPES != null && SUPPORTED_INDEX_VALUE_TYPES.keySet().contains( language ) )
        {
            return SUPPORTED_INDEX_VALUE_TYPES.get( language );
        }

        return null;
    }
}
