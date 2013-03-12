package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
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
    private static final ContentPathNameGenerator CONTENT_PATH_NAME_GENERATOR = new ContentPathNameGenerator();

    public CreateOrUpdateContentRpcHandler()
    {
        super( "content_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedContentTypeName qualifiedContentTypeName =
            new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
        final ContentType contentType = getContentType( qualifiedContentTypeName );
        final RootDataSet rootDataSet = new RootDataSetParser( contentType ).parse( context.param( "contentData" ).required().asObject() );
        final String displayName = context.param( "displayName" ).notBlank().asString();

        final ContentId contentId = contentIdOrNull( context.param( "contentId" ).asString() );
        if ( contentId == null )
        {
            final ContentPath parentContentPath = contentPathOrNull( context.param( "parentContentPath" ).asString() );
            if ( parentContentPath == null )
            {
                context.setResult( new JsonErrorResult( "Missing parameter [contentId] or [parentContentPath]" ) );
                return;
            }

            HandleCreateContent handleCreateContent = new HandleCreateContent();
            handleCreateContent.context = context;
            handleCreateContent.parentContentPath = parentContentPath;
            handleCreateContent.qualifiedContentTypeName = qualifiedContentTypeName;
            handleCreateContent.handleCreateContent( displayName, rootDataSet );
        }
        else
        {
            HandleUpdateContent handleUpdateContent = new HandleUpdateContent();
            handleUpdateContent.contentId = contentId;
            handleUpdateContent.updateContent( displayName, rootDataSet );

            final String newContentName = context.param( "contentName" ).asString();
            final boolean renameContent = !Strings.isNullOrEmpty( newContentName );
            if ( renameContent )
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

    private class HandleCreateContent
    {
        private JsonRpcContext context;

        private ContentPath parentContentPath;

        private QualifiedContentTypeName qualifiedContentTypeName;

        private void handleCreateContent( final String displayName, final RootDataSet rootDataSet )
        {
            try
            {
                final ContentPath newContentPath = resolvePathForNewContent( parentContentPath, displayName );

                final CreateContent createContent = content().create();
                createContent.contentPath( newContentPath );
                createContent.contentType( qualifiedContentTypeName );
                createContent.rootDataSet( rootDataSet );
                createContent.displayName( displayName );
                createContent.owner( AccountKey.anonymous() );
                final ContentId newContentId = client.execute( createContent );

                context.setResult( CreateOrUpdateContentJsonResult.created( newContentId, newContentPath ) );
            }
            catch ( ContentNotFoundException e )
            {
                context.setResult( new JsonErrorResult( "Unable to create content. Path [{0}] does not exist", parentContentPath ) );
            }
        }

        // TODO: This logic possibly belongs in CreateContentHandler, since not only RPC API would benefit from this
        private ContentPath resolvePathForNewContent( final ContentPath parentPath, final String displayName )
        {
            ContentPath possibleNewPath = ContentPath.from( parentPath, CONTENT_PATH_NAME_GENERATOR.generatePathName( displayName ) );
            int i = 1;
            while ( contentExists( possibleNewPath ) )
            {
                i++;
                possibleNewPath = ContentPath.from( parentPath, CONTENT_PATH_NAME_GENERATOR.generatePathName( displayName + "-" + i ) );
            }
            return possibleNewPath;
        }

    }

    private class HandleUpdateContent
    {
        private ContentId contentId;

        private void updateContent( final String displayName, final RootDataSet rootDataSet )
        {
            final UpdateContent updateContent = content().update();
            updateContent.selector( contentId );
            updateContent.editor( composite( setContentData( rootDataSet ), setContentDisplayName( displayName ) ) );
            updateContent.modifier( AccountKey.anonymous() );

            client.execute( updateContent );
        }
    }

    private ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentTypes =
            Commands.contentType().get().names( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        final ContentType contentType = client.execute( getContentTypes ).first();
        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );
        return contentType;
    }

    private boolean contentExists( final ContentPath contentPath )
    {
        final Contents contents = client.execute( content().get().selectors( ContentPaths.from( contentPath ) ) );
        return contents.isNotEmpty();
    }

    private ContentId contentIdOrNull( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return ContentId.from( value );
    }

    private ContentPath contentPathOrNull( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return ContentPath.from( value );
    }
}
