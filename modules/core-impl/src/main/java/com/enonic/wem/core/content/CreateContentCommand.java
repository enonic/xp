package com.enonic.wem.core.content;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.NamePrettyfier;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentCreatedEvent;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.CreateContentTranslatorParams;
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.media.MediaInfo;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistAtPathException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

final class CreateContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private final CreateContentParams params;

    private final MediaInfo mediaInfo;

    private CreateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfo = builder.mediaInfo;
    }

    Content execute()
    {
        return doExecute();
    }

    private Content doExecute()
    {
        validateContentTypeProperties();

        final CreateContentParams processedContent = runContentProcessors();

        final CreateContentTranslatorParams createContentTranslatorParams = createContentTranslatorParams( processedContent );

        final CreateNodeParams createNodeParams = translator.toCreateNodeParams( createContentTranslatorParams );

        try
        {
            final Node createdNode = nodeService.create( createNodeParams );
            eventPublisher.publish( new ContentCreatedEvent( ContentId.from( createdNode.id().toString() ) ) );
            return translator.fromNode( createdNode );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new ContentAlreadyExistException(
                ContentPath.from( createContentTranslatorParams.getParent(), createContentTranslatorParams.getName().toString() ) );
        }
    }

    private CreateContentTranslatorParams createContentTranslatorParams( final CreateContentParams processedContent )
    {
        final CreateContentTranslatorParams.Builder builder = CreateContentTranslatorParams.create( processedContent );
        builder.valid( checkIsValid( processedContent ) );
        populateName( builder );
        populateCreator( builder );
        builder.owner( getDefaultOwner( processedContent ) );

        return builder.build();
    }

    private CreateContentParams runContentProcessors()
    {
        return new ProxyContentProcessor( mediaInfo ).processCreate( params );
    }

    private void validateContentTypeProperties()
    {
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );

        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Content type not found [" + params.getType().toString() + "]" );
        }
        if ( contentType.isAbstract() )
        {
            throw new IllegalArgumentException( "Cannot create content with an abstract type [" + params.getType().toString() + "]" );
        }

        final ContentPath parentPath = params.getParent();
        if ( !parentPath.isRoot() )
        {
            final Content parent = getContent( parentPath );
            if ( parent == null )
            {
                throw new IllegalArgumentException(
                    "Content could not be created. Children not allowed in parent [" + parentPath.toString() + "]" );
            }
            final ContentType parentContentType =
                contentTypeService.getByName( new GetContentTypeParams().contentTypeName( parent.getType() ) );
            if ( !parentContentType.allowChildContent() )
            {
                throw new IllegalArgumentException(
                    "Content could not be created. Children not allowed in parent [" + parentPath.toString() + "]" );
            }
        }
    }

    private PrincipalKey getDefaultOwner( final CreateContentParams createContentParams )
    {
        if ( createContentParams.getOwner() == null )
        {
            PrincipalKey user = getCurrentPrincipalKey();

            return PrincipalKey.ofAnonymous().equals( user ) ? null : user;
        }
        else
        {
            return createContentParams.getOwner();
        }
    }

    private PrincipalKey getCurrentPrincipalKey()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        return authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();
    }

    private void populateName( final CreateContentTranslatorParams.Builder builder )
    {
        if ( params.getName() == null || StringUtils.isEmpty( params.getName().toString() ) )
        {
            builder.name( NamePrettyfier.create( params.getDisplayName() ) );
        }
    }

    private void populateCreator( final CreateContentTranslatorParams.Builder builder )
    {
        final User currentUser = getCurrentUser();
        builder.creator( currentUser.getKey() );
    }

    private boolean checkIsValid( final CreateContentParams contentParams )
    {
        final DataValidationErrors dataValidationErrors = ValidateContentDataCommand.create().
            contentData( contentParams.getData() ).
            contentType( contentParams.getType() ).
            metadatas( contentParams.getMetadata() != null ? Metadatas.from( contentParams.getMetadata() ) : Metadatas.empty() ).
            mixinService( this.mixinService ).
            moduleService( this.moduleService ).
            contentTypeService( this.contentTypeService ).
            build().
            execute();

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            if ( params.isRequireValid() )
            {
                throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    static Builder create()
    {
        return new Builder();
    }

    static Builder create( AbstractCreatingOrUpdatingContentCommand source )
    {
        return new Builder( source );
    }

    static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private CreateContentParams params;

        private MediaInfo mediaInfo;

        private Builder()
        {
        }

        private Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
        }

        Builder params( final CreateContentParams params )
        {
            this.params = params;
            return this;
        }

        Builder mediaInfo( final MediaInfo value )
        {
            this.mediaInfo = value;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params, "params must be given" );
        }

        public CreateContentCommand build()
        {
            validate();
            return new CreateContentCommand( this );
        }
    }

}
