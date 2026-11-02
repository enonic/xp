package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentQueryNodeQueryTranslatorTest
{
    @Test
    void contentRootIsFilteredByPathHierarchyWithoutScoring()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate(
            ContentQuery.create().queryExpr( QueryParser.parse( "displayName = 'a' ORDER BY _path ASC" ) ).build() ).build();

        assertEquals( "displayname = 'a'", nodeQuery.getQuery().getConstraint().toString() );
        assertEquals( "_path ASC", nodeQuery.getQuery().getOrderList().get( 0 ).toString() );
        assertContentRootFilter( nodeQuery, "/content" );
    }

    @Test
    void queryWithoutExpressionMatchesContentRootByFilterOnly()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build();

        assertNull( nodeQuery.getQuery().getConstraint() );
        assertContentRootFilter( nodeQuery, "/content" );
    }

    @Test
    void contentRootFilterFollowsContextOfEachQuery()
    {
        final NodeQuery archived = ContextBuilder.from( ContextAccessor.current() )
            .attribute( ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE, ArchiveConstants.ARCHIVE_ROOT_PATH )
            .build()
            .callWith( () -> ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build() );
        final NodeQuery content = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build();

        assertContentRootFilter( archived, "/archive" );
        assertContentRootFilter( content, "/content" );
    }

    private static void assertContentRootFilter( final NodeQuery nodeQuery, final String root )
    {
        final BooleanFilter filter = nodeQuery.getQueryFilters()
            .stream()
            .filter( BooleanFilter.class::isInstance )
            .map( BooleanFilter.class::cast )
            .findFirst()
            .orElseThrow();
        final ValueFilter hierarchy = (ValueFilter) filter.getMust().iterator().next();
        assertEquals( "_path._path", hierarchy.getFieldName() );
        assertEquals( List.of( root ), hierarchy.getValues().stream().map( Value::asString ).collect( Collectors.toList() ) );
        final ValueFilter contentRoot = (ValueFilter) filter.getMustNot().iterator().next();
        assertEquals( "_path", contentRoot.getFieldName() );
        assertEquals( List.of( root ), contentRoot.getValues().stream().map( Value::asString ).collect( Collectors.toList() ) );
    }
}
