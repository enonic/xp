package com.enonic.xp.core.impl.content;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.name.NamePrettyfier;
import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentAccessException;
import com.enonic.xp.core.content.ContentAlreadyExistException;
import com.enonic.xp.core.content.ContentChangeEvent;
import com.enonic.xp.core.content.ContentConstants;
import com.enonic.xp.core.content.ContentCreatedEvent;
import com.enonic.xp.core.content.ContentDataValidationException;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.CreateContentParams;
import com.enonic.xp.core.content.CreateContentTranslatorParams;
import com.enonic.xp.core.content.Metadatas;
import com.enonic.xp.core.context.ContextAccessor;
import com.enonic.xp.core.media.MediaInfo;
import com.enonic.xp.core.node.CreateNodeParams;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeAccessException;
import com.enonic.xp.core.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.core.schema.content.ContentType;
import com.enonic.xp.core.schema.content.GetContentTypeParams;
import com.enonic.xp.core.schema.content.validator.DataValidationError;
import com.enonic.xp.core.schema.content.validator.DataValidationErrors;
import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.User;
import com.enonic.xp.core.security.auth.AuthenticationInfo;

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

        final ContentType type = this.contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );

        final CreateContentParams processedContent = runContentProcessors( type );

        final CreateContentTranslatorParams createContentTranslatorParams = createContentTranslatorParams( processedContent );

        final CreateNodeParams createNodeParams = translator.toCreateNodeParams( createContentTranslatorParams );

        try
        {
            final Node createdNode = nodeService.create( createNodeParams );
            final Content createdContent = translator.fromNode( createdNode );
            eventPublisher.publish( new ContentCreatedEvent( ContentId.from( createdNode.id().toString() ) ) );
            eventPublisher.publish( ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.CREATE, createdContent.getPath() ) );

            return createdContent;
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new ContentAlreadyExistException(
                ContentPath.from( createContentTranslatorParams.getParent(), createContentTranslatorParams.getName().toString() ) );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private CreateContentTranslatorParams createContentTranslatorParams( final CreateContentParams processedContent )
    {
        final CreateContentTranslatorParams.Builder builder = CreateContentTranslatorParams.create( processedContent );
        builder.valid( checkIsValid( processedContent ) );
        populateName( builder );
        populateCreator( builder );
        setChildOrder( builder );
        builder.owner( getDefaultOwner( processedContent ) );
        populateLanguage( builder );

        return builder.build();
    }

    private void setChildOrder( final CreateContentTranslatorParams.Builder builder )
    {
        builder.childOrder( this.params.getChildOrder() != null ? this.params.getChildOrder() : ContentConstants.DEFAULT_CHILD_ORDER );
    }

    private CreateContentParams runContentProcessors( ContentType contentType )
    {
        return ProxyContentProcessor.create().
            mediaInfo( mediaInfo ).
            contentType( contentType ).
            mixinService( mixinService ).
            build().
            processCreate( params );
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

    private void populateLanguage( final CreateContentTranslatorParams.Builder builder )
    {
        Locale language = getDefaultLanguage( params );
        if ( language != null )
        {
            builder.language( language );
        }
    }

    private Locale getDefaultLanguage( final CreateContentParams createContentParams )
    {
        ContentPath parentPath = createContentParams.getParent();
        if ( createContentParams.getLanguage() == null && !parentPath.isRoot() )
        {
            final Content parent = getContent( parentPath );

            return parent != null ? parent.getLanguage() : null;
        }
        else
        {
            return createContentParams.getLanguage();
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
