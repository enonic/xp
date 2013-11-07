package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandHandler;


public final class CreateContentTypeHandler
    extends CommandHandler<CreateContentType>
{

    private final static ContentTypeNodeTranslator CONTENT_TYPE_NODE_TRANSLATOR = new ContentTypeNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {

        validate( ContentTypeName.from( command.getName() ), command.getSuperType() );

        final CreateNode createNodeCommand = CONTENT_TYPE_NODE_TRANSLATOR.toCreateNodeCommand( command );
        final CreateNodeResult createNodeResult = context.getClient().execute( createNodeCommand );

        final ContentType createdContentType = CONTENT_TYPE_NODE_TRANSLATOR.fromNode( createNodeResult.getPersistedNode() );
        command.setResult( createdContentType.getContentTypeName() );
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
}
