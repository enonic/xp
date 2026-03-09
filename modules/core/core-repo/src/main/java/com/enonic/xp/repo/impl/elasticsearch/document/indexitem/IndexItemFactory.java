package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.OrderbyValueResolver;
import com.enonic.xp.repo.impl.index.IndexLanguageController;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemFactory
{
    public static List<IndexItem<?>> create( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
    {
        Objects.requireNonNull( indexPath );
        Objects.requireNonNull( value );

        Value processedPropertyValue = applyValueProcessors( value, indexConfigDocument.getConfigForPath( indexPath ) );

        return createItems( indexPath, indexConfigDocument, processedPropertyValue );
    }

    private static Value applyValueProcessors( final Value value, final IndexConfig indexConfig )
    {
        Value processedPropertyValue = value;

        for ( IndexValueProcessor indexValueProcessor : indexConfig.getIndexValueProcessors() )
        {
            processedPropertyValue = indexValueProcessor.process( processedPropertyValue );
        }
        return processedPropertyValue;
    }

    private static List<IndexItem<?>> createItems( final IndexPath indexPath, final IndexConfigDocument indexConfigDocument,
                                                   final Value processedPropertyValue )
    {
        final ImmutableList.Builder<IndexItem<?>> items = ImmutableList.builder();

        final IndexConfig indexConfig = indexConfigDocument.getConfigForPath( indexPath );

        if ( indexConfig.isEnabled() )
        {
            items.addAll( create( indexPath, processedPropertyValue ) );
            items.addAll( createFullText( indexPath, processedPropertyValue, indexConfig ) );
            items.addAll( createOrderBy( indexPath, processedPropertyValue, indexConfig ) );
            items.addAll( createAllText( processedPropertyValue, indexConfig, indexConfigDocument.getAllTextConfig() ) );
            items.addAll( createStemmed( indexPath, processedPropertyValue, indexConfig ) );

            if ( indexConfig.isPath() )
            {
                items.add( createPath( indexPath, processedPropertyValue ) );
            }
        }

        return items.build();
    }

    static List<IndexItem<String>> createAllText( final Value propertyValue, final IndexConfig indexConfig,
                                                  final AllTextIndexConfig allTextIndexConfig )
    {
        final ImmutableList.Builder<IndexItem<String>> allTextItems = ImmutableList.builder();

        if ( !allTextIndexConfig.isEnabled() )
        {
            return allTextItems.build();
        }

        if ( indexConfig.isDecideByType() && propertyValue.isText() || indexConfig.isIncludeInAllText() )
        {
            final var text = propertyValue.asString();
            if ( allTextIndexConfig.isFulltext() )
            {
                allTextItems.add( new IndexItem<>( NodeIndexPath.ALL_TEXT, text, IndexValueType.ANALYZED ) );
            }

            if ( allTextIndexConfig.isnGram() )
            {
                allTextItems.add( new IndexItem<>( NodeIndexPath.ALL_TEXT, text, IndexValueType.NGRAM ) );
            }

            allTextIndexConfig.getLanguages()
                .stream()
                .map( IndexLanguageController::resolveStemmedIndexValueType )
                .map( type -> new IndexItem<>( NodeIndexPath.ALL_TEXT, text, type ) )
                .forEach( allTextItems::add );
        }

        return allTextItems.build();
    }

    static List<IndexItem<String>> createFullText( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        final ImmutableList.Builder<IndexItem<String>> fulltextItems = ImmutableList.builder();

        if ( indexConfig.isDecideByType() )
        {
            if ( value.isText() )
            {
                final var text = value.asString();
                fulltextItems.add( new IndexItem<>( indexPath, text, IndexValueType.ANALYZED ) );
                fulltextItems.add( new IndexItem<>( indexPath, text, IndexValueType.NGRAM ) );
            }
        }
        else
        {
            if ( indexConfig.isFulltext() )
            {
                fulltextItems.add( new IndexItem<>( indexPath, value.asString(), IndexValueType.ANALYZED ) );
            }

            if ( indexConfig.isnGram() )
            {
                fulltextItems.add( new IndexItem<>( indexPath, value.asString(), IndexValueType.NGRAM ) );
            }
        }

        return fulltextItems.build();
    }

    static List<IndexItem<?>> createOrderBy( final IndexPath indexPath, final Value propertyValue, final IndexConfig indexConfig )
    {
        final List<IndexItem<?>> items = new ArrayList<>();

        final var orderByValue = OrderbyValueResolver.getOrderbyValue( propertyValue );

        items.add( new IndexItem<>( indexPath, orderByValue, IndexValueType.ORDERBY ) );

        indexConfig.getLanguages()
            .forEach( language -> items.add(
                new IndexItem<>( indexPath, orderByValue, IndexLanguageController.resolveOrderByIndexValueType( language ) ) ) );
        return items;
    }

    static IndexItem<String> createPath( final IndexPath indexPath, final Value propertyValue )
    {
        return new IndexItem<>( indexPath, propertyValue.asString(), IndexValueType.PATH );
    }

    static List<IndexItem<?>> createStemmed( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        final ImmutableList.Builder<IndexItem<?>> stemmedItems = ImmutableList.builder();

        if ( indexConfig.isStemmed() )
        {
            final var indexValue = value.asString();
            indexConfig.getLanguages()
                .forEach( language -> stemmedItems.add(
                    new IndexItem<>( indexPath, indexValue, IndexLanguageController.resolveStemmedIndexValueType( language ) ) ) );
        }

        return stemmedItems.build();
    }

    static List<IndexItem<?>> create( final IndexPath indexPath, final Value value )
    {
        final ImmutableList.Builder<IndexItem<?>> baseTypes = ImmutableList.builder();

        if ( value.isDateType() )
        {
            baseTypes.add( new IndexItem<>( indexPath, value.asInstant(), IndexValueType.DATETIME ) );
        }
        else if ( value.isNumericType() )
        {
            baseTypes.add( new IndexItem<>( indexPath, value.asDouble(), IndexValueType.NUMBER ) );
        }
        else if ( value.isGeoPoint() )
        {
            baseTypes.add( new IndexItem<>( indexPath, value.asString(), IndexValueType.GEO_POINT ) );
        }

        baseTypes.add( new IndexItem<>( indexPath, value.asString(), IndexValueType.STRING ) );

        return baseTypes.build();
    }
}
