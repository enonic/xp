package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSelectorQueryJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentRelativePathResolver;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
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
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.site.Site;

public class ContentSelectorQueryJsonToContentQueryConverter
{
    final private ContentSelectorQueryJson contentQueryJson;

    final private ContentService contentService;

    final private RelationshipTypeService relationshipTypeService;

    final private ContentTypeService contentTypeService;

    final private Content content;

    private Site parentSite;

    private final static FieldExpr PATH_FIELD_EXPR = FieldExpr.from( NodeIndexPath.PATH );

    private final static String ALLOW_PATH_CONFIG_ENTRY = "allowPath";

    private final static String ALLOW_CONTENT_TYPE_CONFIG_ENTRY = "allowContentType";

    private final static String RELATIONSHIP_TYPE_CONFIG_ENTRY = "relationshipType";

    private ContentSelectorQueryJsonToContentQueryConverter( final Builder builder )
    {
        this.contentQueryJson = builder.contentQueryJson;
        this.contentService = builder.contentService;
        this.relationshipTypeService = builder.relationshipTypeService;
        this.contentTypeService = builder.contentTypeService;
        this.content = contentService.getById( contentQueryJson.getContentId() );
    }

    public ContentQuery createQuery()
    {
        final Input contentSelectorInput =
            this.getContentSelectorInputFromContentType( this.getContentType( content.getType() ), contentQueryJson.getInputName() );

        final ContentQuery.Builder builder = ContentQuery.create().
            from( this.contentQueryJson.getFrom() ).
            size( this.contentQueryJson.getSize() ).
            queryExpr( this.createQueryExpr( contentSelectorInput ) ).
            addContentTypeNames( this.getContentTypeNames( contentSelectorInput ) );

        return builder.build();
    }

    private QueryExpr createQueryExpr( final Input contentSelectorInput )
    {
        final List<String> allowedPaths = this.getAllowedPaths( contentSelectorInput );

        if ( allowedPaths.size() > 0 )
        {
            this.resolveParentSiteIfNeeded( allowedPaths );
            return this.constructExprWithAllowedPaths( allowedPaths );
        }
        else
        {
            return QueryParser.parse( this.contentQueryJson.getQueryExprString() );
        }
    }

    private void resolveParentSiteIfNeeded( final List<String> allowedPaths )
    {
        if ( ContentRelativePathResolver.anyPathNeedsSiteResolving( allowedPaths ) )
        {
            this.parentSite = this.contentService.getNearestSite( this.content.getId() );
        }
    }

    private QueryExpr constructExprWithAllowedPaths( final List<String> allowedPaths )
    {

        ConstraintExpr expr = null;

        for ( String allowedPath : allowedPaths )
        {
            expr = this.addAllowPathToExpr( allowedPath, expr );
        }

        return this.addSearchQueryToExpr( expr );
    }

    private QueryExpr addSearchQueryToExpr( final ConstraintExpr expr )
    {
        if ( !Strings.isNullOrEmpty( this.contentQueryJson.getQueryExprString() ) )
        {
            final QueryExpr searchQueryExpr = QueryParser.parse( this.contentQueryJson.getQueryExprString() );
            if ( expr != null )
            {
                final ConstraintExpr andExpr = LogicalExpr.and( expr, searchQueryExpr.getConstraint() );
                return QueryExpr.from( andExpr, searchQueryExpr.getOrderList() );
            }
            return searchQueryExpr;
        }

        return expr == null ? QueryParser.parse( "" ) : QueryExpr.from( expr );
    }

    private ConstraintExpr addAllowPathToExpr( final String allowedPath, final ConstraintExpr expr )
    {
        String resolvedPath;
        if ( ContentRelativePathResolver.hasSiteToResolve( allowedPath ) )
        {
            if ( this.parentSite != null )
            {
                resolvedPath = ContentRelativePathResolver.resolveWithSite( allowedPath, this.parentSite );
            }
            else
            {
                return expr;
            }
        }
        else
        {
            resolvedPath = ContentRelativePathResolver.resolve( this.content, allowedPath );
        }
        return expr == null ? createCompareExp( resolvedPath ) : LogicalExpr.or( expr, createCompareExp( resolvedPath ) );
    }

    private CompareExpr createCompareExp( final String resolvedPath )
    {
        return CompareExpr.like( PATH_FIELD_EXPR, createValueExpr( resolvedPath ) );
    }

    private ValueExpr createValueExpr( final String resolvedPath )
    {
        return ValueExpr.string( "/" + ContentConstants.CONTENT_ROOT_NAME + resolvedPath );
    }

    private ContentTypeNames getContentTypeNames( final Input contentSelectorInput )
    {
        if ( contentSelectorInput != null )
        {
            ContentTypeNames contentTypeNames =
                ContentTypeNames.from( contentSelectorInput.getInputTypeConfig().getProperties( ALLOW_CONTENT_TYPE_CONFIG_ENTRY ).
                    stream().
                    map( ( prop ) -> ContentTypeName.from( prop.getValue() ) ).
                    collect( Collectors.toList() ) );

            if ( contentTypeNames.getSize() == 0 )
            {
                contentTypeNames = getContentTypeNamesFromRelationship( contentSelectorInput );
            }
            return contentTypeNames;
        }
        return ContentTypeNames.empty();
    }

    private ContentTypeNames getContentTypeNamesFromRelationship( final Input contentSelectorInput )
    {
        String relationshipConfigEntry = contentSelectorInput.getInputTypeConfig().getValue( RELATIONSHIP_TYPE_CONFIG_ENTRY );
        if ( relationshipConfigEntry != null )
        {
            final RelationshipType relationshipType =
                relationshipTypeService.getByName( RelationshipTypeName.from( relationshipConfigEntry ) );
            if ( relationshipType != null )
            {
                return ContentTypeNames.from( relationshipType.getAllowedToTypes() );
            }
        }
        return ContentTypeNames.empty();
    }

    private List<String> getAllowedPaths( final Input contentSelectorInput )
    {
        if ( contentSelectorInput != null )
        {
            return contentSelectorInput.getInputTypeConfig().getProperties( ALLOW_PATH_CONFIG_ENTRY ).
                stream().
                map( ( prop ) -> prop.getValue() ).
                collect( Collectors.toList() );
        }
        return ImmutableList.of();
    }

    private Input getContentSelectorInputFromContentType( final ContentType contentType, final String inputName )
    {
        final FormItem formItem = getInputFormItem( contentType, inputName );

        return getContentSelectorInputFromFormItem( formItem );
    }

    private FormItem getInputFormItem( final ContentType contentType, final String inputName )
    {
        if ( contentType != null )
        {
            final FormItems inputFormItems = getInputFormItemsOrBasicFormItemsIfPresent( contentType );
            if ( inputFormItems != null )
            {
                return inputFormItems.getItemByName( inputName );
            }
        }
        return null;
    }

    private FormItems getInputFormItemsOrBasicFormItemsIfPresent( final ContentType contentType )
    {
        final FormItem basicFormItem = contentType.getForm().getFormItems().getItemByName( "basic" );
        if ( basicFormItem != null && basicFormItem instanceof FieldSet )
        {
            return ( (FieldSet) basicFormItem ).getFormItems();
        }
        return contentType.getForm().getFormItems();
    }

    private Input getContentSelectorInputFromFormItem( final FormItem formItem )
    {
        if ( formItem != null && FormItemType.INPUT.equals( formItem.getType() ) )
        {
            Input input = (Input) formItem;
            if ( InputTypeName.CONTENT_SELECTOR.equals( input.getInputType() ) ||
                InputTypeName.IMAGE_SELECTOR.equals( input.getInputType() ) )
            {
                return input;
            }
        }
        return null;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
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

        public Builder contentQueryJson( final ContentSelectorQueryJson contentQueryJson )
        {
            this.contentQueryJson = contentQueryJson;
            return this;
        }

        public Builder contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
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

        public ContentSelectorQueryJsonToContentQueryConverter build()
        {
            return new ContentSelectorQueryJsonToContentQueryConverter( this );
        }
    }
}
