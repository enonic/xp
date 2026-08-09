package com.enonic.xp.core.impl.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.node.FieldValues;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.sortvalues.SortValuesProperty;

import static java.util.Objects.requireNonNull;

final class FindContentIdsByQueryCommand
    extends AbstractContentCommand
{
    private final ContentQuery query;

    private FindContentIdsByQueryCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    FindContentIdsByQueryResult execute()
    {
        final ContentQueryParent parent;
        if ( ContentQueryParent.isSpecifiedIn( this.query ) )
        {
            parent = ContentQueryParent.resolve( this.query, this );
            if ( parent == null )
            {
                return FindContentIdsByQueryResult.create()
                    .contents( ContentIds.empty() )
                    .aggregations( Aggregations.empty() )
                    .build();
            }
        }
        else
        {
            parent = null;
        }

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.query, parent ).
            addQueryFilters( createFilters() ).
            build();

        final Map<ContentId, HighlightedProperties> highlight = new LinkedHashMap<>();

        final Map<ContentId, SortValuesProperty> sortValues = new LinkedHashMap<>();

        final Map<ContentId, Float> scoreValues = new LinkedHashMap<>();

        final Map<ContentId, FieldValues> fields = new LinkedHashMap<>();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        result.getNodeHits().forEach( nodeHit -> {
            final ContentId contentId = ContentId.from( nodeHit.getNodeId() );

            scoreValues.put( contentId, nodeHit.getScore() );

            if ( nodeHit.getHighlight() != null && !nodeHit.getHighlight().isEmpty() )
            {
                highlight.put( contentId, nodeHit.getHighlight() );
            }

            if ( nodeHit.getSort() != null && nodeHit.getSort().getValues() != null && !nodeHit.getSort().getValues().isEmpty() )
            {
                sortValues.put( contentId, nodeHit.getSort() );
            }

            if ( !nodeHit.getFields().isEmpty() )
            {
                fields.put( contentId, translatePathValues( nodeHit.getFields() ) );
            }
        } );

        return FindContentIdsByQueryResult.create().
            contents( ContentNodeHelper.toContentIds( result.getNodeIds() ) ).
            aggregations( result.getAggregations() ).
            highlight( highlight ).
            sort( sortValues ).
            score( scoreValues ).
            fields( fields ).
            totalHits( result.getTotalHits() ).
            build();
    }

    // the index stores node paths; at the content level path fields come back as content paths, like everywhere else in this API
    private static FieldValues translatePathValues( final FieldValues fields )
    {
        final Set<String> fieldNames = fields.getFields();
        if ( !fieldNames.contains( NodeIndexPath.PATH.getPath() ) )
        {
            return fields;
        }

        final FieldValues.Builder translated = FieldValues.create();
        for ( final String field : fieldNames )
        {
            if ( field.equals( NodeIndexPath.PATH.getPath() ) )
            {
                translated.add( field, fields.getValues( field )
                    .stream()
                    .map( value -> ContentNodeHelper.translateNodePathToContentPath( new NodePath( value.toString() ) ).toString() )
                    .collect( Collectors.toList() ) );
            }
            else
            {
                translated.add( field, fields.getValues( field ) );
            }
        }
        return translated.build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentQuery query;

        private Builder()
        {
        }

        public Builder query( final ContentQuery query )
        {
            this.query = query;
            return this;
        }

        public FindContentIdsByQueryCommand build()
        {
            validate();
            return new FindContentIdsByQueryCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( query, "query is required" );
        }

    }
}
