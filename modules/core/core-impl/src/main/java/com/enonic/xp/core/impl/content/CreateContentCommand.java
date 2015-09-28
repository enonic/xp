package com.enonic.xp.core.impl.content;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistException;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentCreatedEvent;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.core.impl.content.validate.DataValidationError;
import com.enonic.xp.core.impl.content.validate.DataValidationErrors;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

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
        validateBlockingChecks();

        CreateContentParams processedParams = processCreateContentParams();

        final CreateContentTranslatorParams createContentTranslatorParams = createContentTranslatorParams( processedParams );

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

    private void validateBlockingChecks()
    {
        validateSpecificChecks( params );
        validateContentType( params );
        validateCreationAllowance( params );
        validatePropertyTree( params );
    }

    private void validateSpecificChecks( final CreateContentParams params )
    {
        if ( params.getType().isTemplateFolder() )
        {
            validateCreateTemplateFolder( params );
        }
        else if ( params.getType().isPageTemplate() )
        {
            validateCreatePageTemplate( params );
        }
    }

    private void validateCreateTemplateFolder( final CreateContentParams params )
    {
        try
        {
            final Content parent = GetContentByPathCommand.create( params.getParent() ).
                nodeService( this.nodeService ).
                contentTypeService( this.contentTypeService ).
                translator( this.translator ).
                eventPublisher( this.eventPublisher ).
                build().
                execute();

            if ( !parent.getType().isSite() )
            {
                final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
                throw new IllegalArgumentException( "A template folder can only be created below a content of type 'site'. Path: " + path );
            }
        }
        catch ( ContentNotFoundException e )
        {
            final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
            throw new IllegalArgumentException(
                "Parent folder not found; A template folder can only be created below a content of type 'site'. Path: " + path, e );
        }
    }

    private void validateCreatePageTemplate( final CreateContentParams params )
    {
        try
        {
            final Content parent = GetContentByPathCommand.create( params.getParent() ).
                nodeService( this.nodeService ).
                contentTypeService( this.contentTypeService ).
                translator( this.translator ).
                eventPublisher( this.eventPublisher ).
                build().
                execute();

            if ( !parent.getType().isTemplateFolder() )
            {
                final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
                throw new IllegalArgumentException(
                    "A page template can only be created below a content of type 'template-folder'. Path: " + path );
            }
        }
        catch ( ContentNotFoundException e )
        {
            final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
            throw new IllegalArgumentException(
                "Parent not found; A page template can only be created below a content of type 'template-folder'. Path: " + path, e );
        }
    }

    private void validateContentType( final CreateContentParams params )
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
    }

    private void validateCreationAllowance( final CreateContentParams params )
    {
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

    private void validatePropertyTree( final CreateContentParams params )
    {
        if ( !params.getType().isUnstructured() )
        {
            final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );

            try
            {
                InputValidator.
                    create().
                    contentType( contentType ).
                    inputTypeResolver( InputTypes.BUILTIN ).
                    build().
                    validate( params.getData() );
            }
            catch ( final Exception e )
            {
                final String name = params.getName() == null ? "" : params.getName().toString();
                final ContentPath path = ContentPath.from( params.getParent(), name );
                throw new IllegalArgumentException( "Incorrect property for content: " + path, e );
            }
        }
    }

    private CreateContentParams processCreateContentParams()
    {
        final ContentType type = this.contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );
        return runContentProcessors( this.params, type );
    }

    private CreateContentTranslatorParams createContentTranslatorParams( final CreateContentParams processedContent )
    {
        final CreateContentTranslatorParams.Builder builder = CreateContentTranslatorParams.create( processedContent );
        builder.valid( validateNonBlockingChecks( processedContent ) );
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

    private CreateContentParams runContentProcessors( final CreateContentParams createContentParams, final ContentType contentType )
    {
        return ProxyContentProcessor.create().
            mediaInfo( mediaInfo ).
            contentType( contentType ).
            mixinService( mixinService ).
            build().
            processCreate( createContentParams );
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
            if ( !Strings.isNullOrEmpty( params.getDisplayName() ) )
            {
                builder.name( NamePrettyfier.create( params.getDisplayName() ) );
            }
            else
            {
                builder.displayName( "" );
                builder.name( ContentName.unnamed() );
            }
        }
    }

    private void populateCreator( final CreateContentTranslatorParams.Builder builder )
    {
        final User currentUser = getCurrentUser();
        builder.creator( currentUser.getKey() );
    }

    private boolean validateNonBlockingChecks( final CreateContentParams contentParams )
    {
        final DataValidationErrors dataValidationErrors = ValidateContentDataCommand.create().
            contentData( contentParams.getData() ).
            contentType( contentParams.getType() ).
            extradatas( contentParams.getExtraDatas() != null ? ExtraDatas.from( contentParams.getExtraDatas() ) : ExtraDatas.empty() ).
            mixinService( this.mixinService ).
            siteService( this.siteService ).
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

        @Override
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
