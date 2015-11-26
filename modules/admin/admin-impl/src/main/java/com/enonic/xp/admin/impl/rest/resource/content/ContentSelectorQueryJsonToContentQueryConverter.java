package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSelectorQueryJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
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

public class ContentSelectorQueryJsonToContentQueryConverter
{
    final private ContentSelectorQueryJson contentQueryJson;

    final private ContentService contentService;

    final private RelationshipTypeService relationshipTypeService;

    final private ContentTypeService contentTypeService;

    final private Content content;

    private ContentSelectorQueryJsonToContentQueryConverter( final Builder builder )
    {
        this.contentQueryJson = builder.contentQueryJson;
        this.contentService = builder.contentService;
        this.relationshipTypeService = builder.relationshipTypeService;
        this.contentTypeService = builder.contentTypeService;
        this.content = contentService.getById( contentQueryJson.getContentId() );
    }

    public ContentQuery makeContentQuery()
    {

        final Input contentSelectorInput =
            this.getContentSelectorInputFromContentType( this.getContentType( content.getType() ), contentQueryJson.getInputName() );

        final ContentQuery.Builder builder = ContentQuery.create().
            from( this.contentQueryJson.getFrom() ).
            size( this.contentQueryJson.getSize() ).
            queryExpr( this.makeQueryExpr( contentSelectorInput ) ).
            addContentTypeNames( this.getContentTypeNames( contentSelectorInput ) );

        return builder.build();
    }

    private QueryExpr makeQueryExpr( final Input contentSelectorInput )
    {
        final List<String> allowedPaths = this.getAllowedPaths( contentSelectorInput );
        if ( allowedPaths.size() > 0 )
        {
            return this.constructExprWithAllowedPaths( allowedPaths );
        }
        else
        {
            return QueryParser.parse( this.contentQueryJson.getQueryExprString() );
        }
    }

    private QueryExpr constructExprWithAllowedPaths( final List<String> allowedPaths )
    {
        final String firstPath = allowedPaths.get( 0 );

        ConstraintExpr expr =
            CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( "/content" + makePathFromAllowedPathEntry( firstPath ) ) );

        for ( String allowedPath : allowedPaths )
        {
            if ( !allowedPath.equals( firstPath ) )
            {
                ConstraintExpr likeExpr = CompareExpr.like( FieldExpr.from( "_path" ),
                                                            ValueExpr.string( "/content" + makePathFromAllowedPathEntry( allowedPath ) ) );
                expr = LogicalExpr.or( expr, likeExpr );
            }
        }

        if ( StringUtils.isNotEmpty( this.contentQueryJson.getQueryExprString() ) )
        {
            final QueryExpr searchQueryExpr = QueryParser.parse( this.contentQueryJson.getQueryExprString() );
            expr = LogicalExpr.and( expr, searchQueryExpr.getConstraint() );
            return QueryExpr.from( expr, searchQueryExpr.getOrderList() );
        }

        return QueryExpr.from( expr );
    }

    private String makePathFromAllowedPathEntry( final String path )
    {
        if ( "*".equals( path ) || "/".equals( path ) || "/*".equals( path ) )
        {
            return "/*"; // any path
        }
        else if ( "./".equals( path ) || "./*".equals( path ) )
        {
            return this.content.getPath() + "/*"; // all children of current item
        }
        else if ( "../".equals( path ) || "../*".equals( path ) )
        {
            return this.content.getParentPath().isRoot()
                ? this.content.getParentPath() + "*"
                : this.content.getParentPath() + "/*"; // siblings and children of current item
        }
        else if ( path.startsWith( "../" ) )
        {
            return makeEndWithStar( getPathStartedSomeLevelsHigher( path ) ); // path starting x levels higher
        }
        else
        {
            return makeEndWithStar( makeStartWithSlash( path ) );
        }
    }

    private String getPathStartedSomeLevelsHigher( final String path )
    {
        int levels = getNumberOfLevelsToAscend( path );
        ContentPath contentPath = this.content.getPath();
        for ( int level = 1; level <= levels; level++ )
        {
            contentPath = contentPath.getParentPath();
            if ( contentPath.isRoot() )
            {
                return path.substring( levels * 3 );
            }
        }
        return contentPath.toString() + "/" + path.substring( levels * 3 );
    }

    private int getNumberOfLevelsToAscend( final String path )
    {
        if ( path.startsWith( "../" ) )
        {
            return getNumberOfLevelsToAscend( path.substring( 3, path.length() ) ) + 1;
        }
        return 0;
    }

    private String makeStartWithSlash( final String str )
    {
        if ( str.startsWith( "/" ) )
        {
            return str;
        }
        else
        {
            return "/" + str;
        }
    }

    private String makeEndWithStar( final String str )
    {
        if ( str.endsWith( "*" ) )
        {
            return str;
        }
        else
        {
            return str + "*";
        }
    }

    private ContentTypeNames getContentTypeNames( final Input contentSelectorInput )
    {
        if ( contentSelectorInput != null )
        {
            ContentTypeNames contentTypeNames =
                ContentTypeNames.from( contentSelectorInput.getInputTypeConfig().getProperties( "allowContentType" ).
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
        String relationshipConfigEntry = contentSelectorInput.getInputTypeConfig().getValue( "relationshipType" );
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
            return contentSelectorInput.getInputTypeConfig().getProperties( "allowPath" ).
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
            final FormItem basicFormItem = contentType.getForm().getFormItems().getItemByName( "basic" );
            if ( basicFormItem != null && basicFormItem instanceof FieldSet )
            {
                return ( (FieldSet) basicFormItem ).getFormItems().getItemByName( inputName );
            }
        }
        return null;
    }

    private Input getContentSelectorInputFromFormItem( final FormItem formItem )
    {
        if ( formItem != null && FormItemType.INPUT.equals( formItem.getType() ) )
        {
            Input input = (Input) formItem;
            if ( InputTypeName.CONTENT_SELECTOR.equals( input.getInputType() ) )
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
