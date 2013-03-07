package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.core.content.ContentPathNameGenerator;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.content.editor.ContentEditors.composite;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentData;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentDisplayName;

@Component
public final class CreateOrUpdateContentRpcHandler
    extends AbstractDataRpcHandler
{
    final ContentPathNameGenerator contentPathNameGenerator = new ContentPathNameGenerator();

    public CreateOrUpdateContentRpcHandler()
    {
        super( "content_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String idParam = context.param( "contentId" ).asString();
        final String parentContentPathParam = context.param( "parentContentPath" ).asString();
        final String newContentName = context.param( "contentName" ).asString();

        final ContentPath parentContentPath;
        final ContentId contentId;
        if ( idParam != null )
        {
            contentId = ContentId.from( idParam );
            parentContentPath = null;
        }
        else if ( parentContentPathParam != null )
        {
            parentContentPath = ContentPath.from( parentContentPathParam );
            contentId = null;
        }
        else
        {
            context.setResult( new JsonErrorResult( "Missing parameter [contentId] or [parentContentPath]" ) );
            return;
        }

        final QualifiedContentTypeName qualifiedContentTypeName =
            new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
        final String displayName = context.param( "displayName" ).notBlank().asString();

        final ContentType contentType = getContentType( qualifiedContentTypeName );

        final RootDataSet rootDataSet = new RootDataSetParser( contentType ).parse( context.param( "contentData" ).required().asObject() );

        if ( contentId == null )
        {
            try
            {
                final ContentPath newContentPath = getPathForNewContent( parentContentPath, displayName );
                final ContentId newContentId = createContent( qualifiedContentTypeName, newContentPath, displayName, rootDataSet );
                context.setResult( CreateOrUpdateContentJsonResult.created( newContentId, newContentPath ) );
            }
            catch ( ContentNotFoundException e )
            {
                context.setResult(
                    new JsonErrorResult( "Unable to create content. Path [{0}] does not exist", parentContentPath.toString() ) );
            }
        }
        else
        {
            updateContent( contentId, displayName, rootDataSet );
            if ( !Strings.isNullOrEmpty( newContentName ) )
            {
                try
                {
                    renameContent( contentId, newContentName );
                }
                catch ( ContentAlreadyExistException e )
                {
                    context.setResult(
                        new JsonErrorResult( "Unable to rename content. Content with path [{0}] already exists.", e.getContentPath() ) );
                    return;
                }
            }
            context.setResult( CreateOrUpdateContentJsonResult.updated() );
        }
    }

    private void renameContent( final ContentId contentId, final String newContentName )
    {
        final RenameContent renameContent = content().rename().contentId( contentId ).newName( newContentName );
        client.execute( renameContent );
    }

    private ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentTypes =
            Commands.contentType().get().names( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        final ContentType contentType = client.execute( getContentTypes ).first();
        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );
        return contentType;
    }

    private ContentId createContent( final QualifiedContentTypeName qualifiedContentTypeName, ContentPath contentPath,
                                     final String displayName, final RootDataSet rootDataSet )
    {
        final CreateContent createContent = content().create();
        createContent.contentPath( contentPath );
        createContent.contentType( qualifiedContentTypeName );
        createContent.rootDataSet( rootDataSet );
        createContent.displayName( displayName );
        createContent.owner( AccountKey.anonymous() );
        return client.execute( createContent );
    }

    // TODO: This logic possibly belongs in CreateContentHandler, since not only RPC API would benefit from this
    private ContentPath getPathForNewContent( final ContentPath parentPath, final String displayName )
    {
        ContentPath contentPath = ContentPath.from( parentPath, contentPathNameGenerator.generatePathName( displayName ) );
        int i = 1;
        while ( contentExists( contentPath ) )
        {
            i++;
            contentPath = ContentPath.from( parentPath, contentPathNameGenerator.generatePathName( displayName + "-" + i ) );
        }
        return contentPath;
    }

    private void updateContent( final ContentId contentId, final String displayName, final RootDataSet rootDataSet )
    {
        final UpdateContents updateContents = content().update();
        updateContents.selectors( ContentIds.from( contentId ) );
        updateContents.editor( composite( setContentData( rootDataSet ), setContentDisplayName( displayName ) ) );
        updateContents.modifier( AccountKey.anonymous() );

        client.execute( updateContents );
    }

    private boolean contentExists( final ContentPath contentPath )
    {
        final Contents contents = client.execute( content().get().selectors( ContentPaths.from( contentPath ) ) );
        return contents.isNotEmpty();
    }
}
