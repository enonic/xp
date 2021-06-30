package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSelectorQueryJson;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentRelativePathResolver;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.site.Site;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ContentSelectorQueryJsonToContentQueryConverter
{
    private final ContentSelectorQueryJson contentQueryJson;

    private final ContentService contentService;

    private final ContentTypeService contentTypeService;

    private final RelationshipTypeService relationshipTypeService;

    private final Content content;

    private final ApplicationWildcardMatcher.Mode contentTypeParseMode;

    private Site parentSite;

    private static final FieldExpr PATH_FIELD_EXPR = FieldExpr.from( NodeIndexPath.PATH );

    private ContentSelectorQueryJsonToContentQueryConverter( final Builder builder )
    {
        this.contentQueryJson = builder.contentQueryJson;
        this.contentService = builder.contentService;
        this.relationshipTypeService = builder.relationshipTypeService;
        this.content = contentQueryJson.getContentId() != null ? contentService.getById( contentQueryJson.getContentId() ) : null;
        this.contentTypeService = builder.contentTypeService;
        this.contentTypeParseMode = builder.contentTypeParseMode;
    }

    public ContentQuery createQuery()
    {
        final ContentQuery.Builder builder = ContentQuery.create()
            .from( this.contentQueryJson.getFrom() )
            .size( this.contentQueryJson.getSize() )
            .queryExpr( this.createQueryExpr() )
            .addContentTypeNames( this.getContentTypeNamesFromJson() );

        return builder.build();
    }

    private ContentTypeNames getContentTypeNamesFromJson()
    {
        List<String> contentTypeNames = this.contentQueryJson.getContentTypeNames();
        if ( contentTypeNames.isEmpty() )
        {
            return this.getContentTypeNamesFromRelationshipType();
        }

        if ( this.content != null )
        {
            final ApplicationWildcardMatcher<ContentTypeName> wildcardMatcher =
                new ApplicationWildcardMatcher<>( this.content.getType().getApplicationKey(), ContentTypeName::toString, this.contentTypeParseMode );

            final Predicate<ContentTypeName> filter =
                contentTypeNames.stream().map( wildcardMatcher::createPredicate ).reduce( Predicate::or ).orElse( s -> false );
            return ContentTypeNames.from(
                contentTypeService.getAll().stream().map( ContentType::getName ).filter( filter ).collect( Collectors.toList() ) );
        }

        return ContentTypeNames.from( contentTypeNames );
    }

    private ContentTypeNames getContentTypeNamesFromRelationshipType()
    {
        if ( this.contentQueryJson.getRelationshipType() == null )
        {
            return ContentTypeNames.empty();
        }

        final RelationshipType relationshipType =
            relationshipTypeService.getByName( RelationshipTypeName.from( this.contentQueryJson.getRelationshipType() ) );
        return getContentTypeNamesFromRelationship( relationshipType );
    }

    private QueryExpr createQueryExpr()
    {
        final List<String> allowedPaths = this.contentQueryJson.getAllowedContentPaths();

        if ( allowedPaths.size() == 0 )
        {
            return QueryParser.parse( this.contentQueryJson.getQueryExprString() );
        }

        if ( this.content != null )
        {
            this.resolveParentSiteIfNeeded( allowedPaths );
        }

        return this.constructExprWithAllowedPaths( allowedPaths );
    }

    private void resolveParentSiteIfNeeded( final List<String> allowedPaths )
    {
        if ( ContentRelativePathResolver.anyPathNeedsSiteResolving( allowedPaths ) )
        {
            this.parentSite = this.contentService.getNearestSite( this.content.getId() );

            if ( this.parentSite == null )
            {
                throw new RuntimeException( "Could not resolve parent site for content: " + this.content.getDisplayName() );
            }
        }
    }

    private QueryExpr constructExprWithAllowedPaths( final List<String> allowedPaths )
    {

        ConstraintExpr expr = null;

        for ( String allowedPath : allowedPaths )
        {
            expr = this.addAllowPathToExpr( allowedPath, expr );
        }

        if ( isNullOrEmpty( this.contentQueryJson.getQueryExprString() ) )
        {
            return constraintExprToQueryExpr( expr );
        }

        return this.addSearchQueryToExpr( expr );
    }

    private QueryExpr constraintExprToQueryExpr( final ConstraintExpr expr )
    {
        return expr == null ? QueryParser.parse( "" ) : QueryExpr.from( expr );
    }

    private QueryExpr addSearchQueryToExpr( final ConstraintExpr expr )
    {

        final QueryExpr searchQueryExpr = QueryParser.parse( this.contentQueryJson.getQueryExprString() );

        if ( expr == null )
        {
            return searchQueryExpr;
        }

        final ConstraintExpr andExpr = LogicalExpr.and( expr, searchQueryExpr.getConstraint() );
        return QueryExpr.from( andExpr, searchQueryExpr.getOrderList() );
    }

    private ConstraintExpr addAllowPathToExpr( final String allowedPath, final ConstraintExpr expr )
    {
        if ( ContentRelativePathResolver.hasSiteToResolve( allowedPath ) && this.parentSite == null )
        {
            return expr; // do nothing - we can't resolve site
        }

        final String resolvedPath = doResolvePath( allowedPath );

        return createAndAppendExpr( resolvedPath, expr );
    }

    private String doResolvePath( final String allowedPath )
    {
        if ( ContentRelativePathResolver.hasSiteToResolve( allowedPath ) )
        {
            return ContentRelativePathResolver.resolveWithSite( allowedPath, this.parentSite );
        }
        return ContentRelativePathResolver.resolve( this.content, allowedPath );
    }

    private ConstraintExpr createAndAppendExpr( final String resolvedPath, final ConstraintExpr expr )
    {
        return expr == null ? createCompareExpr( resolvedPath ) : LogicalExpr.or( expr, createCompareExpr( resolvedPath ) );
    }

    private CompareExpr createCompareExpr( final String resolvedPath )
    {
        return CompareExpr.like( PATH_FIELD_EXPR, createValueExpr( resolvedPath ) );
    }

    private ValueExpr createValueExpr( final String resolvedPath )
    {
        return ValueExpr.string( "/" + ContentConstants.CONTENT_ROOT_NAME + resolvedPath );
    }

    private ContentTypeNames getContentTypeNamesFromRelationship( final RelationshipType relationshipType )
    {
        if ( relationshipType == null )
        {
            return ContentTypeNames.empty();
        }
        return ContentTypeNames.from( relationshipType.getAllowedToTypes() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ContentSelectorQueryJson contentQueryJson;

        private ContentService contentService;

        private RelationshipTypeService relationshipTypeService;

        private ContentTypeService contentTypeService;

        private ApplicationWildcardMatcher.Mode contentTypeParseMode;

        public Builder contentQueryJson( final ContentSelectorQueryJson contentQueryJson )
        {
            this.contentQueryJson = contentQueryJson;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder relationshipTypeService( final RelationshipTypeService relationshipTypeService )
        {
            this.relationshipTypeService = relationshipTypeService;
            return this;
        }

        public Builder contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return this;
        }

        public Builder contentTypeParseMode( final ApplicationWildcardMatcher.Mode contentTypeParseMode )
        {
            this.contentTypeParseMode = contentTypeParseMode;
            return this;
        }

        private void validate() {
            Preconditions.checkNotNull( contentQueryJson, "contentQueryJson must be set" );
            Preconditions.checkNotNull( contentTypeParseMode, "contentTypeParseMode must be set" );
            Preconditions.checkNotNull( relationshipTypeService, "relationshipTypeService must be set" );
            Preconditions.checkNotNull( contentService, "contentService must be set" );
        }

        public ContentSelectorQueryJsonToContentQueryConverter build()
        {
            validate();
            return new ContentSelectorQueryJsonToContentQueryConverter( this );
        }
    }
}
