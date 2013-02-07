package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.core.content.ContentPathNameGenerator;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

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
        final String contentPathParam = context.param( "contentPath" ).asString();
        final String parentContentPathParam = context.param( "parentContentPath" ).asString();
        final ContentPath contentPath;
        final ContentPath parentContentPath;
        if ( contentPathParam != null )
        {
            contentPath = ContentPath.from( contentPathParam );
            parentContentPath = null;
        }
        else if ( parentContentPathParam != null )
        {
            parentContentPath = ContentPath.from( parentContentPathParam );
            contentPath = null;
        }
        else
        {
            context.setResult( new JsonErrorResult( "Missing parameter [contentPath] or [parentContentPath]" ) );
            return;
        }

        final QualifiedContentTypeName qualifiedContentTypeName =
            new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
        final String displayName = context.param( "displayName" ).notBlank().asString();

        final ContentType contentType = getContentType( qualifiedContentTypeName );

        final RootDataSet rootDataSet = new RootDataSetParser( contentType ).parse( context.param( "contentData" ).required().asObject() );

        if ( contentPath == null )
        {
            try
            {
                final ContentPath newContentPath = getPathForNewContent( parentContentPath, displayName );
                final ContentId contentId = doCreateContent( qualifiedContentTypeName, newContentPath, displayName, rootDataSet );
                context.setResult( CreateOrUpdateContentJsonResult.created( contentId, newContentPath ) );
            }
            catch ( ContentNotFoundException e )
            {
                context.setResult(
                    new JsonErrorResult( "Unable to create content. Path [{0}] does not exist", parentContentPath.toString() ) );
            }
        }
        else
        {
            doUpdateContent( contentPath, displayName, rootDataSet );
            context.setResult( CreateOrUpdateContentJsonResult.updated() );
        }
    }

    private ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentTypes =
            Commands.contentType().get().names( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        return client.execute( getContentTypes ).first();
    }

    private ContentId doCreateContent( final QualifiedContentTypeName qualifiedContentTypeName, ContentPath contentPath,
                                       final String displayName, final RootDataSet rootDataSet )
    {
        final CreateContent createContent = Commands.content().create();
        createContent.contentPath( contentPath );
        createContent.contentType( qualifiedContentTypeName );
        createContent.rootDataSet( rootDataSet );
        createContent.displayName( displayName );
        createContent.owner( AccountKey.anonymous() );
        return client.execute( createContent );
    }

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

    private void doUpdateContent( final ContentPath contentPath, final String displayName, final RootDataSet rootDataSet )
    {
        final UpdateContents updateContents = Commands.content().update();
        updateContents.selectors( ContentPaths.from( contentPath ) );
        updateContents.editor( composite( setContentData( rootDataSet ), setContentDisplayName( displayName ) ) );
        updateContents.modifier( AccountKey.anonymous() );

        client.execute( updateContents );
    }

    private boolean contentExists( final ContentPath contentPath )
    {
        final Contents contents = client.execute( Commands.content().get().selectors( ContentPaths.from( contentPath ) ) );
        return contents.isNotEmpty();
    }
}
