package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.CompareContentParams;
import com.enonic.wem.api.content.CompareContentResult;
import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.CompareContentsParams;
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
import com.enonic.wem.api.content.PushContentParams;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.index.query.QueryService;

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
    private QueryService queryService;

    @Inject
    private ContentNodeTranslator contentNodeTranslator;

    @Override
    public Content getById( final ContentId id, final Context context )
    {
        return GetContentByIdCommand.create( id ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Contents getByIds( final GetContentByIdsParams params, final Context context )
    {
        return GetContentByIdsCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content getByPath( final ContentPath path, final Context context )
    {
        return GetContentByPathCommand.create( path ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Contents getByPaths( final ContentPaths paths, final Context context )
    {
        return GetContentByPathsCommand.create( paths ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Contents getRoots( final Context context )
    {
        return GetRootContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Contents getByParent( final ContentPath parentPath, final Context context )
    {
        return GetContentByParentCommand.create( parentPath ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content create( final CreateContentParams params, final Context context )
    {
        return CreateContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            params( params ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateContentParams params, final Context context )
    {
        return UpdateContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            attachmentService( this.attachmentService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public DeleteContentResult delete( final DeleteContentParams params, final Context context )
    {
        return DeleteContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            params( params ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content push( final PushContentParams params, final Context context )
    {
        params.getContentId();

        return PushContentCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            contentId( params.getContentId() ).
            target( params.getTarget() ).
            context( context ).
            build().
            execute();
    }

    @Override
    public DataValidationErrors validate( final ValidateContentData data, final Context context )
    {
        return new ValidateContentDataCommand().
            contentTypeService( this.contentTypeService ).
            data( data ).
            execute();
    }

    @Override
    public Content rename( final RenameContentParams params, final Context context )
    {
        return RenameContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.contentNodeTranslator ).
            context( context ).
            build().
            execute();
    }

    @Override
    public ContentQueryResult find( final ContentQuery contentQuery, final Context context )
    {
        return new FindContentCommand().
            contentQuery( contentQuery ).
            queryService( this.queryService ).
            context( context ).
            execute();
    }

    @Override
    public CompareContentResult compare( final CompareContentParams params, final Context context )
    {
        return CompareContentCommand.create().
            nodeService( this.nodeService ).
            context( context ).
            contentId( params.getContentId() ).
            target( params.getTarget() ).
            build().
            execute();
    }

    @Override
    public CompareContentResults compare( final CompareContentsParams params, final Context context )
    {
        return CompareContentsCommand.create().
            nodeService( this.nodeService ).
            context( context ).
            contentIds( params.getContentIds() ).
            target( params.getTarget() ).
            build().
            execute();
    }


    @Override
    public String generateContentName( final String displayName )
    {
        return new ContentPathNameGenerator().generatePathName( displayName );
    }
}
