package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.content.processor.ProcessCreateParams;
import com.enonic.xp.content.processor.ProcessCreateResult;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.data.Property;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.google.common.base.Strings.isNullOrEmpty;

final class CreateContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private final CreateContentParams params;

    private final MediaInfo mediaInfo;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    private CreateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfo = builder.mediaInfo;
        this.formDefaultValuesProcessor = builder.formDefaultValuesProcessor;
    }

    static Builder create()
    {
        return new Builder();
    }

    static Builder create( AbstractCreatingOrUpdatingContentCommand source )
    {
        return new Builder( source );
    }

    Content execute()
    {
        return doExecute();
    }

    private Content doExecute()
    {
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );
        validateContentType( contentType );

        formDefaultValuesProcessor.setDefaultValues( contentType.getForm(), params.getData() );
        // TODO apply default values to xData

        CreateContentParams processedParams = runContentProcessors( this.params, contentType );

        validateBlockingChecks( processedParams );

        final CreateContentTranslatorParams createContentTranslatorParams = createContentTranslatorParams( processedParams );

        final CreateNodeParams createNodeParams = CreateNodeParamsFactory.create( createContentTranslatorParams )
            .contentTypeService( this.contentTypeService )
            .pageDescriptorService( this.pageDescriptorService )
            .xDataService( this.xDataService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .contentDataSerializer( this.translator.getContentDataSerializer() )
            .siteService( this.siteService )
            .build()
            .produce().refresh( params.isRefresh() ? RefreshMode.ALL : RefreshMode.STORAGE ).build();

        final Node createdNode;
        try
        {
            createdNode = nodeService.create( createNodeParams );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new ContentAlreadyExistsException(
                ContentPath.from( createContentTranslatorParams.getParent(), createContentTranslatorParams.getName() ),
                e.getRepositoryId(), e.getBranch() );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }

        return translator.fromNode( createdNode, false );
    }

    private void validateBlockingChecks( final CreateContentParams params )
    {
        validateParentChildRelations( params.getParent(), params.getType() );
        validatePropertyTree( params );
        validateCreateAttachments( params.getCreateAttachments() );
    }

    private void validateContentType( final ContentType contentType )
    {
        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Content type not found [" + params.getType().toString() + "]" );
        }
        if ( contentType.isAbstract() )
        {
            throw new IllegalArgumentException( "Cannot create content with an abstract type [" + params.getType().toString() + "]" );
        }
    }

    private void validatePropertyTree( final CreateContentParams params )
    {
        if ( !params.getType().isUnstructured() )
        {
            final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );

            try
            {
                InputValidator.create()
                    .form( contentType.getForm() )
                    .inputTypeResolver( InputTypes.BUILTIN )
                    .build()
                    .validate( params.getData() );
            }
            catch ( final Exception e )
            {
                throw new IllegalArgumentException( "Incorrect content property", e );
            }
        }
    }

    private CreateContentTranslatorParams createContentTranslatorParams( final CreateContentParams processedContent )
    {
        final CreateContentTranslatorParams.Builder builder = CreateContentTranslatorParams.create( processedContent )
            .creator( getCurrentUser().getKey() )
            .owner( getDefaultOwner( processedContent ) );
        populateName( builder );
        setChildOrder( builder );
        populateLanguage( builder );

        populateValid( builder );

        return builder.build();
    }

    private void setChildOrder( final CreateContentTranslatorParams.Builder builder )
    {
        builder.childOrder( this.params.getChildOrder() != null ? this.params.getChildOrder() : ContentConstants.DEFAULT_CHILD_ORDER );
    }

    private CreateContentParams runContentProcessors( final CreateContentParams createContentParams, final ContentType contentType )
    {
        CreateContentParams processedParams = createContentParams;

        for ( final ContentProcessor contentProcessor : this.contentProcessors )
        {
            if ( contentProcessor.supports( contentType ) )
            {
                final ProcessCreateResult result = contentProcessor.processCreate( new ProcessCreateParams( processedParams, mediaInfo ) );

                processedParams = CreateContentParams.create( result.getCreateContentParams() ).build();
            }
        }

        return processedParams;
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
        if ( createContentParams.getLanguage() == null )
        {
            final Node parent = nodeService.getByPath( ContentNodeHelper.translateContentPathToNodePath( createContentParams.getParent() ) );

            final List<Property> inheritProperties = parent.data().getProperties( ContentPropertyNames.INHERIT );
            final boolean inherited =
                inheritProperties.stream().anyMatch( property -> ContentInheritType.CONTENT.name().equals( property.getString() ) );

            final String language;

            if ( inherited )
            {
                final Node contentRootNode =
                    runAsAdmin( () -> this.nodeService.getByPath( ContentNodeHelper.translateContentPathToNodePath( ContentPath.ROOT ) ) );

                language = contentRootNode.data().getString( ContentPropertyNames.LANGUAGE );
            }
            else
            {
                language = parent.data().getString( ContentPropertyNames.LANGUAGE );
            }

            return language != null ? Locale.forLanguageTag( language ) : null;
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
        if ( params.getName() == null || isNullOrEmpty( params.getName().toString() ) )
        {
            if ( !isNullOrEmpty( params.getDisplayName() ) )
            {
                builder.name( NamePrettyfier.create( params.getDisplayName() ) );
            }
            else
            {
                builder.displayName( "" );
                builder.name( ContentName.uniqueUnnamed() );
            }
        }
    }

    private void populateValid( final CreateContentTranslatorParams.Builder builder )
    {
        final ValidationErrors validationErrors = ValidateContentDataCommand.create()
            .data( builder.getData() )
            .extraDatas( builder.getExtraDatas() )
            .contentTypeName( builder.getType() )
            .contentName( builder.getName() )
            .displayName( builder.getDisplayName() )
            .createAttachments( builder.getCreateAttachments() )
            .contentValidators( this.contentValidators )
            .contentTypeService( this.contentTypeService )
            .build()
            .execute();

        if ( params.isRequireValid() )
        {
            validationErrors.stream().findFirst().ifPresent( validationError -> {
                throw new ContentDataValidationException( validationError.getMessage() );
            } );
        }

        builder.valid( !validationErrors.hasErrors() );
        builder.validationErrors( validationErrors );
    }

    static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private CreateContentParams params;

        private MediaInfo mediaInfo;

        private FormDefaultValuesProcessor formDefaultValuesProcessor;

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

        Builder formDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
        {
            this.formDefaultValuesProcessor = formDefaultValuesProcessor;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params, "params must be given" );
            Preconditions.checkNotNull( formDefaultValuesProcessor );
            ContentPublishInfoPreconditions.check( params.getContentPublishInfo() );
        }

        public CreateContentCommand build()
        {
            validate();
            return new CreateContentCommand( this );
        }
    }

}
