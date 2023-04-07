package com.enonic.xp.core.impl.content;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.User;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMapping;

import static com.google.common.base.Strings.nullToEmpty;

class AbstractCreatingOrUpdatingContentCommand
    extends AbstractContentCommand
{
    private static final ImmutableList<MediaType> BINARY_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "application", "force-download" ),
                          MediaType.create( "application", "x-force-download" ) );

    private static final ImmutableList<MediaType> EXECUTABLE_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "text", "plain" ), MediaType.create( "application", "x-bzip2" ) );

    final XDataService xDataService;

    final SiteService siteService;

    final List<ContentProcessor> contentProcessors;

    final List<ContentValidator> contentValidators;

    final boolean allowUnsafeAttachmentNames;

    final FormDefaultValuesProcessor formDefaultValuesProcessor;

    AbstractCreatingOrUpdatingContentCommand( final Builder<?> builder )
    {
        super( builder );
        this.xDataService = builder.xDataService;
        this.siteService = builder.siteService;
        this.contentProcessors = List.copyOf( builder.contentProcessors );
        this.contentValidators = List.copyOf( builder.contentValidators );
        this.allowUnsafeAttachmentNames = builder.allowUnsafeAttachmentNames;
        this.formDefaultValuesProcessor = builder.formDefaultValuesProcessor;
    }

    public static class Builder<B extends Builder<B>>
        extends AbstractContentCommand.Builder<B>
    {
        XDataService xDataService;

        SiteService siteService;

        List<ContentProcessor> contentProcessors = List.of();

        List<ContentValidator> contentValidators = List.of();

        boolean allowUnsafeAttachmentNames;

        FormDefaultValuesProcessor formDefaultValuesProcessor;

        Builder()
        {
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
            this.xDataService = source.xDataService;
            this.siteService = source.siteService;
            this.contentProcessors = source.contentProcessors;
            this.contentValidators = source.contentValidators;
        }

        @SuppressWarnings("unchecked")
        B xDataService( final XDataService xDataService )
        {
            this.xDataService = xDataService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B siteService( final SiteService siteService )
        {
            this.siteService = siteService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B contentProcessors( final List<ContentProcessor> contentProcessors )
        {
            this.contentProcessors = contentProcessors;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B contentValidators( final List<ContentValidator> contentValidators )
        {
            this.contentValidators = contentValidators;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B allowUnsafeAttachmentNames( final boolean allowUnsafeAttachmentNames )
        {
            this.allowUnsafeAttachmentNames = allowUnsafeAttachmentNames;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B formDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
        {
            this.formDefaultValuesProcessor = formDefaultValuesProcessor;
            return (B) this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( xDataService, "xDataService cannot be null" );
        }
    }

    void validateCreateAttachments( final CreateAttachments createAttachments )
    {
        if ( !allowUnsafeAttachmentNames && createAttachments != null )
        {
            for ( CreateAttachment attachment : createAttachments )
            {
                if ( !FileNames.isSafeFileName( attachment.getName() ) )
                {
                    throw new IllegalArgumentException( "Unsafe attachment name " + attachment.getName() );
                }
            }
        }
    }

    User getCurrentUser()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    protected boolean isBinaryContentType( final String contentType )
    {
        final MediaType mediaType = MediaType.parse( contentType );
        return BINARY_CONTENT_TYPES.stream().anyMatch( mediaType::is );
    }

    protected boolean isExecutableContentType( final String contentType, final String fileName )
    {
        final MediaType mediaType = MediaType.parse( contentType );
        return EXECUTABLE_CONTENT_TYPES.stream().anyMatch( mediaType::is ) && isExecutableFileName( fileName );
    }

    private boolean isExecutableFileName( final String fileName )
    {
        return fileName.endsWith( ".exe" ) || fileName.endsWith( ".msi" ) || fileName.endsWith( ".dmg" ) || fileName.endsWith( ".bat" ) ||
            fileName.endsWith( ".sh" );
    }

    protected Set<ExtraData> getDefaultExtraDatas( final ApplicationKeys applicationKeys, final ContentTypeName contentTypeName )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) );

        Set<ExtraData> result = new HashSet<>();

        result.addAll(
            xDataService.getByNames( contentType.getXData() ).stream().map( this::createExtraData ).collect( Collectors.toSet() ) );
        result.addAll( Objects.requireNonNullElse( applicationKeys, ApplicationKeys.empty() )
                           .stream()
                           .map( siteService::getDescriptor )
                           .filter( Objects::nonNull )
                           .flatMap( siteDescriptor -> siteDescriptor.getXDataMappings()
                               .stream()
                               .filter( xDataMapping -> doFilterXDataMapping( xDataMapping, contentTypeName ) )
                               .map( this::createExtraData ) )
                           .collect( Collectors.toList() ) );

        return result;
    }

    private boolean doFilterXDataMapping( final XDataMapping xDataMapping, final ContentTypeName contentTypeName )
    {
        String wildcard = xDataMapping.getAllowContentTypes();
        ApplicationKey applicationKey = xDataMapping.getXDataName().getApplicationKey();
        return nullToEmpty( wildcard ).isBlank() ||
            new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString, ApplicationWildcardMatcher.Mode.MATCH ).matches(
                wildcard, contentTypeName);
    }

    private ExtraData createExtraData( final XDataMapping xDataMapping )
    {
        XData xData = xDataService.getByName( xDataMapping.getXDataName() );
        return createExtraData( xData );
    }

    private ExtraData createExtraData( final XData xData )
    {
        return new ExtraData( xData.getName(), new PropertyTree() );
    }
}


