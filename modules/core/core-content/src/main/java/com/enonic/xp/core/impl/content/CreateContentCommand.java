package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ProcessCreateParams;
import com.enonic.xp.core.impl.content.processor.ProcessCreateResult;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.core.impl.content.validate.ValidationError;
import com.enonic.xp.core.impl.content.validate.ValidationErrors;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;

final class CreateContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private final CreateContentParams params;

    private final MediaInfo mediaInfo;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private CreateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfo = builder.mediaInfo;
        this.formDefaultValuesProcessor = builder.formDefaultValuesProcessor;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
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
        validateBlockingChecks();

        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );
        formDefaultValuesProcessor.setDefaultValues( contentType.getForm(), params.getData() );
        // TODO apply default values to xData

        CreateContentParams processedParams = processCreateContentParams();

        final CreateContentTranslatorParams createContentTranslatorParams = createContentTranslatorParams( processedParams );

        final CreateNodeParams createNodeParams = CreateNodeParamsFactory.create( createContentTranslatorParams ).
            contentTypeService(this.contentTypeService ).
            pageDescriptorService( this.pageDescriptorService ).
            xDataService( this.xDataService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            siteService( this.siteService ).
            build().produce();

        try
        {
            final Node createdNode = doCreateContent( createNodeParams );

            if ( params.isRefresh() )
            {
                nodeService.refresh( RefreshMode.SEARCH );
            }

            return translator.fromNode( createdNode, false );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new ContentAlreadyExistsException(
                ContentPath.from( createContentTranslatorParams.getParent(), createContentTranslatorParams.getName().toString() ) );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private Node doCreateContent( final CreateNodeParams createNodeParams )
    {
        return nodeService.create( createNodeParams );
    }

    private void validateBlockingChecks()
    {
        validateSpecificChecks( params );
        validateContentType( params );
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
            final Content parent = GetContentByPathCommand.create( params.getParent(), this ).
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
                eventPublisher( this.eventPublisher ).
                translator( this.translator ).
                build().
                execute();

            if ( !parent.getType().isTemplateFolder() )
            {
                final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
                throw new RuntimeException(
                    "A page template can only be created below a content of type 'template-folder'. Path: " + path );
            }
        }
        catch ( ContentNotFoundException e )
        {
            final ContentPath path = ContentPath.from( params.getParent(), params.getName().toString() );
            throw new RuntimeException(
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

    private void validatePropertyTree( final CreateContentParams params )
    {
        if ( !params.getType().isUnstructured() )
        {
            final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );

            try
            {
                InputValidator.
                    create().
                    form( contentType.getForm() ).
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
        populateParentPath( builder );
        populateName( builder );
        populateCreator( builder );
        setChildOrder( builder );
        builder.owner( getDefaultOwner( processedContent ) );
        populateLanguage( builder );

        populateValid( builder );

        return builder.build();
    }

    private Site getNearestSite( final ContentId id )
    {
        return GetNearestSiteCommand.create().
            nodeService( nodeService ).
            contentTypeService( contentTypeService ).
            translator( translator ).
            eventPublisher( eventPublisher ).
            contentId( id ).
            build().
            execute();
    }

    private void setChildOrder( final CreateContentTranslatorParams.Builder builder )
    {
        builder.childOrder( this.params.getChildOrder() != null ? this.params.getChildOrder() : ContentConstants.DEFAULT_CHILD_ORDER );
    }

    private void setContentPublishInfo( final CreateContentTranslatorParams.Builder builder )
    {
        builder.contentPublishInfo( this.params.getContentPublishInfo() );
    }

    private CreateContentParams runContentProcessors( final CreateContentParams createContentParams, final ContentType contentType )
    {
        CreateContentParams processedParams = createContentParams;

        for ( final ContentProcessor contentProcessor : this.contentProcessors )
        {
            if ( contentProcessor.supports( contentType ) )
            {
                final ProcessCreateResult processCreateResult =
                    contentProcessor.processCreate( new ProcessCreateParams( processedParams, mediaInfo ) );

                processedParams = CreateContentParams.create( processCreateResult.getCreateContentParams() ).
                    build();
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

    private void populateParentPath( final CreateContentTranslatorParams.Builder builder )
    {
        final ContentPath parentPath = params.getParent();
        if ( !parentPath.isRoot() )
        {
            final Content parent = getContent( parentPath );

            final ContentType parentContentType =
                contentTypeService.getByName( new GetContentTypeParams().contentTypeName( parent.getType() ) );
            if ( !parentContentType.allowChildContent() )
            {
                if ( parentContentType.getName().isPageTemplate() )
                {
                    final Site nearestSite = getNearestSite( parent.getId() );
                    if ( nearestSite == null )
                    {
                        throw new IllegalArgumentException(
                            "Content could not be created. No valid site for page template [" + parentPath.toString() + "]" );
                    }
                    builder.parent( nearestSite.getPath() );
                    return;
                }
                throw new IllegalArgumentException(
                    "Content could not be created. Children not allowed in parent [" + parentPath.toString() + "]" );
            }
        }
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

    private void populateValid( final CreateContentTranslatorParams.Builder builder )
    {
        final ValidationErrors validationErrors = ValidateContentDataCommand.create().
            contentData( builder.getData() ).
            contentType( builder.getType() ).
            name( builder.getName() ).
            displayName( builder.getDisplayName() ).
            extradatas( builder.getExtraDatas() != null ? ExtraDatas.from( builder.getExtraDatas() ) : ExtraDatas.empty() ).
            xDataService( this.xDataService ).
            siteService( this.siteService ).
            contentTypeService( this.contentTypeService ).
            build().
            execute();

        for ( ValidationError error : validationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( validationErrors.hasErrors() )
        {
            if ( params.isRequireValid() )
            {
                throw new ContentDataValidationException( validationErrors.getFirst().getErrorMessage() );
            }
            else
            {
                builder.valid( false );
                return;
            }
        }

        builder.valid( true );
    }

    static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private CreateContentParams params;

        private MediaInfo mediaInfo;

        private FormDefaultValuesProcessor formDefaultValuesProcessor;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

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

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        Builder partDescriptorService( final PartDescriptorService value )
        {
            this.partDescriptorService = value;
            return this;
        }

        Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params, "params must be given" );
            Preconditions.checkNotNull( formDefaultValuesProcessor );

            final ContentPublishInfo publishInfo = params.getContentPublishInfo();
            if ( publishInfo != null )
            {
                final Instant publishToInstant = publishInfo.getTo();
                if ( publishToInstant != null )
                {
                    final Instant publishFromInstant = publishInfo.getFrom();
                    Preconditions.checkArgument( publishFromInstant != null, "'Publish from' must be set if 'Publish from' is set." );
                    Preconditions.checkArgument( publishToInstant.compareTo( publishFromInstant ) >= 0,
                                                 "'Publish to' must be set after 'Publish from'." );
                }
            }
        }

        public CreateContentCommand build()
        {
            validate();
            return new CreateContentCommand( this );
        }
    }

}
