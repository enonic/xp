package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.OrderByValueResolver;
import com.enonic.xp.repo.impl.index.IndexLanguageController;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

import static java.util.Objects.requireNonNull;

public class IndexItemFactory
{
    public static List<IndexItem<?>> create( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
    {
        requireNonNull( indexPath );
        requireNonNull( value );

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
            final String text = propertyValue.asString();
            if ( allTextIndexConfig.isFulltext() )
            {
                allTextItems.add( new IndexItem<>( NodeIndexPath.ALL_TEXT, text, StaticIndexValueType.ANALYZED ) );
            }

            if ( allTextIndexConfig.isnGram() )
            {
                allTextItems.add( new IndexItem<>( NodeIndexPath.ALL_TEXT, text, StaticIndexValueType.NGRAM ) );
            }

            allTextIndexConfig.getLanguages()
                .stream()
                .map( IndexLanguageController::resolveStemmedIndexValueType )
                .filter( Objects::nonNull )
                .distinct()
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
                final String text = value.asString();
                fulltextItems.add( new IndexItem<>( indexPath, text, StaticIndexValueType.ANALYZED ) );
                fulltextItems.add( new IndexItem<>( indexPath, text, StaticIndexValueType.NGRAM ) );
            }
        }
        else
        {
            if ( indexConfig.isFulltext() )
            {
                fulltextItems.add( new IndexItem<>( indexPath, value.asString(), StaticIndexValueType.ANALYZED ) );
            }

            if ( indexConfig.isnGram() )
            {
                fulltextItems.add( new IndexItem<>( indexPath, value.asString(), StaticIndexValueType.NGRAM ) );
            }
        }

        return fulltextItems.build();
    }

    static List<IndexItem<?>> createOrderBy( final IndexPath indexPath, final Value propertyValue, final IndexConfig indexConfig )
    {
        final String orderByValue = OrderByValueResolver.getOrderByValue( propertyValue );

        final Stream<IndexValueType> languageTypes = propertyValue.isString()
            ? indexConfig.getLanguages().stream().map( IndexLanguageController::resolveOrderByIndexValueType )
            : Stream.empty();

        return Stream.concat( Stream.<IndexValueType>of( StaticIndexValueType.ORDERBY ), languageTypes )
            .distinct()
            .map( type -> new IndexItem<>( indexPath, orderByValue, type ) )
            .collect( Collectors.toList() );
    }

    static IndexItem<String> createPath( final IndexPath indexPath, final Value propertyValue )
    {
        return new IndexItem<>( indexPath, propertyValue.asString(), StaticIndexValueType.PATH );
    }

    static List<IndexItem<?>> createStemmed( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        final ImmutableList.Builder<IndexItem<?>> stemmedItems = ImmutableList.builder();

        if ( indexConfig.isStemmed() )
        {
            final String indexValue = value.asString();
            indexConfig.getLanguages()
                .stream()
                .map( IndexLanguageController::resolveStemmedIndexValueType )
                .filter( Objects::nonNull )
                .distinct()
                .forEach( indexValueType -> stemmedItems.add( new IndexItem<>( indexPath, indexValue, indexValueType ) ) );
        }

        return stemmedItems.build();
    }

    static List<IndexItem<?>> create( final IndexPath indexPath, final Value value )
    {
        final ImmutableList.Builder<IndexItem<?>> baseTypes = ImmutableList.builder();

        if ( value.isDateType() )
        {
            baseTypes.add( new IndexItem<>( indexPath, value.asInstant(), StaticIndexValueType.DATETIME ) );
        }
        else if ( value.isNumericType() )
        {
            baseTypes.add( new IndexItem<>( indexPath, value.asDouble(), StaticIndexValueType.NUMBER ) );
        }
        else if ( value.isGeoPoint() )
        {
            baseTypes.add( new IndexItem<>( indexPath, value.asString(), StaticIndexValueType.GEO_POINT ) );
        }

        baseTypes.add( new IndexItem<>( indexPath, value.asString(), StaticIndexValueType.STRING ) );

        return baseTypes.build();
    }
}
