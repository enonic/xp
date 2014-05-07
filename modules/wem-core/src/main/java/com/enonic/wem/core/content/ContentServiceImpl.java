package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.GetContentByIdsParams;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.index.query.NodeQueryService;

public class ContentServiceImpl
    implements ContentService
{
    @Inject
    private ContentTypeService contentTypeService;

    @Inject
    private NodeService nodeService;

    @Inject
    private BlobService blobService;

    @Inject
    private AttachmentService attachmentService;

    @Inject
    private NodeQueryService nodeQueryService;

    @Override
    public Content getById( final ContentId id )
    {
        return new GetContentByIdCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            contentId( id ).
            execute();
    }

    @Override
    public Contents getByIds( final GetContentByIdsParams params )
    {
        return new GetContentByIdsCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            params( params ).
            execute();
    }

    @Override
    public Content getByPath( final ContentPath path )
    {
        return new GetContentByPathCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            contentPath( path ).
            execute();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths )
    {
        return new GetContentByPathsCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            contentPaths( paths ).
            execute();
    }

    @Override
    public Contents getRoots()
    {
        return new GetRootContentCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            execute();
    }

    @Override
    public Contents getChildren( final ContentPath parentPath )
    {
        return new GetChildContentCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            parentPath( parentPath ).
            execute();
    }

    @Override
    public Content create( final CreateContentParams params )
    {
        return new CreateContentCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            params( params ).
            execute();
    }

    @Override
    public Content update( final UpdateContentParams params )
    {
        return new UpdateContentCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            attachmentService( this.attachmentService ).
            params( params ).
            execute();
    }

    @Override
    public DeleteContentResult delete( final DeleteContentParams params )
    {
        return new DeleteContentCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            params( params ).
            execute();
    }

    @Override
    public DataValidationErrors validate( final ValidateContentData data )
    {
        return new ValidateContentDataCommand().
            contentTypeService( this.contentTypeService ).
            data( data ).
            execute();
    }

    @Override
    public Content rename( final RenameContentParams params )
    {
        return new RenameContentCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            params( params ).
            execute();
    }

    @Override
    public ContentQueryResult find( final ContentQuery contentQuery )
    {
        return new FindContentCommand().
            contentQuery( contentQuery ).
            nodeQueryService( this.nodeQueryService ).
            execute();
    }

    @Override
    public String generateContentName( final String displayName )
    {
        return new ContentPathNameGenerator().generatePathName( displayName );
    }
}
