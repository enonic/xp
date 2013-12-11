package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.CreateNodeHandler;
import com.enonic.wem.core.index.IndexService;


public final class CreateContentTypeHandler
    extends CommandHandler<CreateContentType>
{
    private final static ContentTypeNodeTranslator CONTENT_TYPE_NODE_TRANSLATOR = new ContentTypeNodeTranslator();

    private IndexService indexService;

    @Override
    public void handle()
        throws Exception
    {
        validate( command.getName(), command.getSuperType() );

        final CreateNode createNodeCommand = CONTENT_TYPE_NODE_TRANSLATOR.toCreateNodeCommand( command );

        final CreateNodeResult result = createNode( createNodeCommand );

        final ContentType createdContentType = CONTENT_TYPE_NODE_TRANSLATOR.fromNode( result.getPersistedNode() );

        command.setResult( createdContentType );
    }

    private CreateNodeResult createNode( final CreateNode createNodeCommand )
        throws Exception
    {
        final CreateNodeHandler createNodeHandler = CreateNodeHandler.create().
            command( createNodeCommand ).
            context( this.context ).
            indexService( this.indexService ).
            build();

        createNodeHandler.handle();

        return createNodeCommand.getResult();
    }

    private void validate( final ContentTypeName contentTypeName, final ContentTypeName contentTypeSuperTypeName )
    {

        final ContentTypeSuperTypeValidator validator =
            ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator().client( context.getClient() ).build();
        validator.validate( contentTypeName, contentTypeSuperTypeName );
        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentTypeName, validationResult.getFirst().getErrorMessage() );
    }


    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
