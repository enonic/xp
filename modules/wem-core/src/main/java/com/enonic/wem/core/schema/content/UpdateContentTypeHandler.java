package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.UpdateNodeHandler;
import com.enonic.wem.core.index.IndexService;


public final class UpdateContentTypeHandler
    extends CommandHandler<UpdateContentType>
{
    private final static ContentTypeNodeTranslator CONTENTTYPE_NO_NODE_TRANSLATOR = new ContentTypeNodeTranslator();

    private IndexService indexService;

    @Override
    public void handle()
        throws Exception
    {

        final ContentType persistedContentType =
            context.getClient().execute( Commands.contentType().get().byName().contentTypeName( command.getContentTypeName() ) );

        if ( persistedContentType == null )
        {
            throw new ContentTypeNotFoundException( command.getContentTypeName() );
        }

        final ContentType editedContentType = command.getEditor().edit( persistedContentType );

        if ( editedContentType != null )
        {
            persistedContentType.checkIllegalEdit( editedContentType );
            validate( editedContentType );

            final NodeEditor nodeEditor = CONTENTTYPE_NO_NODE_TRANSLATOR.toNodeEditor( editedContentType );
            UpdateNode updateNodeCommand = CONTENTTYPE_NO_NODE_TRANSLATOR.toUpdateNodeCommand( persistedContentType.getId(), nodeEditor );

            updateNode( updateNodeCommand );

            command.setResult( UpdateContentTypeResult.SUCCESS );
        }

        command.setResult( UpdateContentTypeResult.SUCCESS );
    }

    private UpdateNodeResult updateNode( final UpdateNode updateNodeCommand )
        throws Exception
    {
        UpdateNodeHandler updateNodeHandler =
            UpdateNodeHandler.create().command( updateNodeCommand ).context( this.context ).indexService( this.indexService ).build();

        updateNodeHandler.handle();

        return updateNodeCommand.getResult();
    }

    private void validate( final ContentType contentType )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator().
            client( context.getClient() ).
            build();

        validator.validate( contentType.getName(), contentType.getSuperType() );

        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentType.getName(), validationResult.getFirst().getErrorMessage() );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
