package com.enonic.wem.admin.rest.resource.content;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.model.ContentFindParams;
import com.enonic.wem.admin.rest.resource.content.model.ContentListJson;
import com.enonic.wem.admin.rest.resource.content.model.ContentNameJson;
import com.enonic.wem.admin.rest.resource.content.model.ContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.content.model.DeleteContentJson;
import com.enonic.wem.admin.rest.resource.content.model.DeleteContentParams;
import com.enonic.wem.admin.rest.resource.content.model.FacetedContentSummaryListJson;
import com.enonic.wem.admin.rest.resource.content.model.ValidateContentJson;
import com.enonic.wem.admin.rest.resource.content.model.ValidateContentParams;
import com.enonic.wem.admin.rpc.content.ContentDataParser;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.space.SpaceNames;

@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource
    extends AbstractResource
{
    @GET
    public ContentListJson get( @QueryParam("path") final String path, @QueryParam("version") final Integer version,
                                @QueryParam("contentIds") final List<String> contentIds )
    {
        if ( StringUtils.isNotEmpty( path ) )
        {
            final ContentPath contentPath = ContentPath.from( path );
            return handleGetContentByPath( contentPath, version );
        }
        else
        {
            final String[] stringOfPathIds = contentIds.toArray( new String[contentIds.size()] );
            return handleGetContentByIds( ContentIds.from( stringOfPathIds ) );
        }
    }

    @GET
    @Path("list")
    public ContentSummaryListJson list( @QueryParam("path") String path )
    {
        final ContentPath parentPath = ContentPath.from( path );

        final GetChildContent getChildContent = Commands.content().getChildren();
        getChildContent.parentPath( parentPath );

        final Contents contents = client.execute( getChildContent );
        return new ContentSummaryListJson( contents );
    }

    @POST
    @Path("find")
    @Consumes(MediaType.APPLICATION_JSON)
    public FacetedContentSummaryListJson find( final ContentFindParams params )
    {

        final ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( params.getFulltext() );
        contentIndexQuery.setIncludeFacets( params.isIncludeFacets() );
        contentIndexQuery.setContentTypeNames( QualifiedContentTypeNames.from( params.getContentTypes() ) );
        contentIndexQuery.setSpaceNames( SpaceNames.from( params.getSpaces() ) );

        Set<ContentFindParams.Range> ranges = params.getRanges();
        if ( ranges != null && !ranges.isEmpty() )
        {
            for ( ContentFindParams.Range range : ranges )
            {
                contentIndexQuery.addRange( range.getLower(), range.getUpper() );
            }
        }

        if ( params.isIncludeFacets() )
        {
            contentIndexQuery.setFacets( params.getFacets() );
        }

        final ContentIndexQueryResult contentIndexQueryResult = this.client.execute( Commands.content().find().query( contentIndexQuery ) );

        final Contents contents =
            this.client.execute( Commands.content().get().selectors( ContentIds.from( contentIndexQueryResult.getContentIds() ) ) );

        return new FacetedContentSummaryListJson( contents, contentIndexQueryResult.getFacets() );
    }

    @GET
    @Path("generateName")
    public ContentNameJson generateName( @QueryParam("displayName") final String displayName )
    {

        final GenerateContentName generateContentName = Commands.content().generateContentName().displayName( displayName );

        final String generatedContentName = client.execute( generateContentName );

        return new ContentNameJson( generatedContentName );
    }

    @POST
    @Path("validate")
    public ValidateContentJson validate( final ValidateContentParams params )
    {
        final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( params.getQualifiedContentTypeName() );

        GetContentTypes getContentType =
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        final ContentType contentType = client.execute( getContentType ).first();

        final ContentData contentData = new ContentDataParser( contentType ).parse( params.getContentData() );

        final DataValidationErrors validationErrors =
            client.execute( Commands.content().validate().contentData( contentData ).contentType( qualifiedContentTypeName ) );

        return new ValidateContentJson( validationErrors );
    }

    @POST
    @Path("delete")
    public DeleteContentJson delete( final DeleteContentParams params )
    {
        final ContentPaths contentsToDelete = ContentPaths.from( params.getContentPaths() );

        final DeleteContentJson jsonResult = new DeleteContentJson();

        for ( final ContentPath contentToDelete : contentsToDelete )
        {
            final DeleteContent deleteContent = Commands.content().delete();
            deleteContent.deleter( AccountKey.anonymous() );
            deleteContent.selector( contentToDelete );
            jsonResult.addResult( contentToDelete, client.execute( deleteContent ) );
        }

        return jsonResult;
    }


    private ContentListJson handleGetContentByPath( final ContentPath contentPath, final Integer version )
    {
        final Content content;

        if ( version == null )
        {
            content = findContent( contentPath );
        }
        else
        {
            content = findContentVersion( contentPath, ContentVersionId.of( version ) );
        }

        if ( content != null )
        {
            return new ContentListJson( content );
        }
        throw new NotFoundException( String.format( "Content [%s] was not found", contentPath ) );
    }

    private Content findContent( final ContentPath contentPath )
    {
        final GetContents getContents = Commands.content().get();
        getContents.selectors( ContentPaths.from( contentPath ) );

        final Contents contents = client.execute( getContents );
        return contents.isNotEmpty() ? contents.first() : null;
    }

    private Content findContentVersion( final ContentPath contentPath, final ContentVersionId versionId )
    {
        final GetContentVersion getContentVersion = Commands.content().getVersion();
        getContentVersion.selector( contentPath ).version( versionId );
        return client.execute( getContentVersion );
    }

    private ContentListJson handleGetContentByIds( final ContentIds ids )
    {
        final GetContents getContents = Commands.content().get();
        getContents.selectors( ids );

        final Contents contents = client.execute( getContents );

        if ( contents.isNotEmpty() )
        {
            return new ContentListJson( contents );
        }

        final String missing = Joiner.on( "," ).join( ids );
        throw new NotFoundException( String.format( "Contents [%s] not found", missing ) );
    }

    // delete() @POST
    // create() @POST
    // update() @POST
}
