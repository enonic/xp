package com.enonic.xp.core.impl.content.serializer;

import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.page.Page;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Reference;

import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_BY;
import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.DISPLAY_NAME;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.INHERIT;
import static com.enonic.xp.content.ContentPropertyNames.LANGUAGE;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;
import static com.enonic.xp.content.ContentPropertyNames.ORIGIN_PROJECT;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.PROCESSED_REFERENCES;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_FIRST;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_FROM;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_INFO;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_TO;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.content.ContentPropertyNames.VALID;
import static com.enonic.xp.content.ContentPropertyNames.VALIDATION_ERRORS;
import static com.enonic.xp.content.ContentPropertyNames.VARIANT_OF;
import static com.enonic.xp.content.ContentPropertyNames.WORKFLOW_INFO;
import static com.enonic.xp.content.ContentPropertyNames.WORKFLOW_INFO_CHECKS;
import static com.enonic.xp.content.ContentPropertyNames.WORKFLOW_INFO_STATE;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.COMPONENTS;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

public class ContentDataSerializer
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    private final PageDataSerializer pageDataSerializer;

    private final ExtraDataSerializer extraDataSerializer;

    private final WorkflowInfoSerializer workflowInfoSerializer;

    private final PublishInfoSerializer publishInfoSerializer;

    public ContentDataSerializer( )
    {
        this( new PageDataSerializer() );
    }

    protected ContentDataSerializer( final PageDataSerializer pageDataSerializer )
    {
        this.pageDataSerializer = pageDataSerializer;
        this.extraDataSerializer = new ExtraDataSerializer();
        this.workflowInfoSerializer = new WorkflowInfoSerializer();
        this.publishInfoSerializer = new PublishInfoSerializer();
    }

    public PropertyTree toCreateNodeData( final CreateContentTranslatorParams params )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet contentAsData = propertyTree.getRoot();

        contentAsData.addBoolean( VALID, params.isValid() );

        addValidationErrors( params.getValidationErrors(), contentAsData );

        contentAsData.ifNotNull().addString( DISPLAY_NAME, params.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, params.getType() != null ? params.getType().toString() : null );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, params.getCreatedTime() );
        contentAsData.ifNotNull().addString( CREATOR, params.getCreator().toString() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, params.getModifiedTime() );
        contentAsData.ifNotNull()
            .addString( OWNER, PrincipalKey.ofAnonymous().equals( params.getOwner() ) || params.getOwner() == null
                ? null
                : params.getOwner().toString() );
        contentAsData.ifNotNull().addString( LANGUAGE, params.getLanguage() != null ? params.getLanguage().toLanguageTag() : null );

        contentAsData.addSet( DATA, params.getData().getRoot().copy( contentAsData.getTree() ) );

        addPublishInfo( contentAsData, params.getContentPublishInfo() );
        addWorkflowInfo( contentAsData, params.getWorkflowInfo() );

        final ExtraDatas extraData = params.getExtraDatas();

        if ( extraData != null && !extraData.isEmpty() )
        {
            extraDataSerializer.toData( extraData, contentAsData );
        }

        AttachmentSerializer.create( contentAsData.getTree(), params.getCreateAttachments() );

        addProcessedReferences( contentAsData, params.getProcessedIds() );

        return propertyTree;
    }

    public PropertyTree toNodeData( final Content content )
    {
        final PropertyTree newPropertyTree = new PropertyTree();
        final PropertySet contentAsData = newPropertyTree.getRoot();

        addMetadata( contentAsData, content );
        contentAsData.addSet( DATA, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasExtraData() )
        {
            extraDataSerializer.toData( content.getAllExtraData(), contentAsData );
        }

        applyAttachmentsAsData( content, contentAsData );

        if ( content.getPage() != null )
        {
            pageDataSerializer.toData( content.getPage(), contentAsData );
        }

        addProcessedReferences( contentAsData, content.getProcessedReferences() );

        return newPropertyTree;
    }

    public void toPageData( final Page page, final PropertySet parent )
    {
        pageDataSerializer.toData( page, parent );
    }

    public Page fromPageData( final PropertySet asSet )
    {
        return pageDataSerializer.fromData( asSet );
    }

    public ExtraDatas fromExtraData( final PropertySet asSet )
    {
        return extraDataSerializer.fromData( asSet );
    }

    public Content.Builder<?> fromData( final PropertySet contentAsSet )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( contentAsSet.getString( ContentPropertyNames.TYPE ) );
        final Content.Builder<?> builder = Content.create( contentTypeName );

        builder.displayName( contentAsSet.getString( DISPLAY_NAME ) );
        builder.valid( contentAsSet.getBoolean( VALID ) != null ? contentAsSet.getBoolean( ContentPropertyNames.VALID ) : false );
        builder.data( contentAsSet.getSet( DATA ).toTree() );

        extractUserInfo( contentAsSet, builder );
        extractOwner( contentAsSet, builder );
        extractLanguage( contentAsSet, builder );
        extractExtradata( contentAsSet, builder );
        extractPage( contentAsSet, builder );
        extractAttachments( contentAsSet, builder );
        extractPublishInfo( contentAsSet, builder );
        extractProcessedReferences( contentAsSet, builder );
        extractWorkflowInfo( contentAsSet, builder );
        extractInherit( contentAsSet, builder );
        extractOriginProject( contentAsSet, builder );
        extractValidationErrors( contentAsSet, builder );
        extractOriginalName( contentAsSet, builder );
        extractOriginalParentPath( contentAsSet, builder );
        extractArchivedTime( contentAsSet, builder );
        extractArchivedBy( contentAsSet, builder );
        extractVariantOf( contentAsSet, builder );

        return builder;
    }

    private void addMetadata( final PropertySet contentAsData, final Content content )
    {
        contentAsData.setBoolean( ContentPropertyNames.VALID, content.isValid() );
        addValidationErrors( content.getValidationErrors(), contentAsData );

        contentAsData.ifNotNull().addString( DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, content.getType().toString() );
        contentAsData.ifNotNull().addString( OWNER, content.getOwner() != null ? content.getOwner().toString() : null );
        contentAsData.ifNotNull().addString( LANGUAGE, content.getLanguage() != null ? content.getLanguage().toLanguageTag() : null );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, content.getModifiedTime() );
        contentAsData.ifNotNull().addString( MODIFIER, content.getModifier().toString() );
        contentAsData.ifNotNull().addString( CREATOR, content.getCreator().toString() );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, content.getCreatedTime() );
        contentAsData.ifNotNull()
            .addReference( VARIANT_OF, content.getVariantOf() != null ? new Reference( NodeId.from( content.getVariantOf() ) ) : null );
        contentAsData.ifNotNull()
            .addString( ORIGIN_PROJECT, content.getOriginProject() != null ? content.getOriginProject().toString() : null );
        addPublishInfo( contentAsData, content.getPublishInfo() );
        addWorkflowInfo( contentAsData, content.getWorkflowInfo() );
        addInherit( contentAsData, content.getInherit() );
        contentAsData.ifNotNull()
            .addString( ORIGINAL_NAME, content.getOriginalName() != null ? content.getOriginalName().toString() : null );
        contentAsData.ifNotNull()
            .addString( ORIGINAL_PARENT_PATH, content.getOriginalParentPath() != null ? content.getOriginalParentPath().toString() : null );
        contentAsData.ifNotNull().addInstant( ARCHIVED_TIME, content.getArchivedTime() );
        contentAsData.ifNotNull().addString( ARCHIVED_BY, content.getArchivedBy() != null ? content.getArchivedBy().toString() : null );
    }

    private void addProcessedReferences( final PropertySet contentAsData, final ContentIds processedIds )
    {
        if ( processedIds == null )
        {
            return;
        }
        contentAsData.ifNotNull()
            .addReferences( PROCESSED_REFERENCES, processedIds.stream()
                .map( contentId -> new Reference( NodeId.from( contentId ) ) )
                .toArray( Reference[]::new ) );
    }

    private void addPublishInfo( final PropertySet contentAsData, final ContentPublishInfo data )
    {
        if ( data != null )
        {
            final PropertySet publishInfo = contentAsData.addSet( PUBLISH_INFO );
            publishInfo.addInstant( PUBLISH_FIRST, data.getFirst() );
            publishInfo.addInstant( PUBLISH_FROM, data.getFrom() );
            publishInfo.addInstant( PUBLISH_TO, data.getTo() );
        }
    }

    public static void addWorkflowInfo( final PropertySet contentAsData, final WorkflowInfo data )
    {
        if ( data != null )
        {
            contentAsData.removeProperties( WORKFLOW_INFO );

            final PropertySet workflowInfo = contentAsData.addSet( WORKFLOW_INFO );
            workflowInfo.addString( WORKFLOW_INFO_STATE, data.getState().toString() );

            final PropertySet workflowInfoChecks = workflowInfo.addSet( WORKFLOW_INFO_CHECKS );
            data.getChecks().forEach( ( key, value ) -> workflowInfoChecks.addString( key, value.toString() ) );
        }
    }

    private void addInherit( final PropertySet contentAsData, final Set<ContentInheritType> inherit )
    {
        if ( inherit != null )
        {
            contentAsData.ifNotNull().addStrings( INHERIT, inherit.stream().map( Enum::name ).collect( Collectors.toList() ) );
        }
    }

    private void extractUserInfo( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        builder.creator( PrincipalKey.from( contentAsSet.getString( CREATOR ) ) );
        builder.createdTime( contentAsSet.getInstant( CREATED_TIME ) );
        builder.modifier( contentAsSet.getString( MODIFIER ) != null ? PrincipalKey.from( contentAsSet.getString( MODIFIER ) ) : null );
        builder.modifiedTime( contentAsSet.getInstant( MODIFIED_TIME ) );
    }

    private void extractPublishInfo( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final ContentPublishInfo publishInfo = publishInfoSerializer.serialize( contentAsSet );

        if ( publishInfo != null )
        {
            builder.publishInfo( publishInfo );
        }
    }

    private void extractAttachments( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final Attachments attachments = dataToAttachments( contentAsSet.getSets( ATTACHMENT ) );
        builder.attachments( attachments );

        final Attachment thumbnailAttachment = attachments.byName( AttachmentNames.THUMBNAIL );
        if ( thumbnailAttachment != null )
        {
            final BinaryReference thumbnailBinaryRef = thumbnailAttachment.getBinaryReference();
            final Thumbnail thumbnail =
                Thumbnail.from( thumbnailBinaryRef, thumbnailAttachment.getMimeType(), thumbnailAttachment.getSize() );
            builder.thumbnail( thumbnail );
        }
    }

    private void extractPage( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        if ( contentAsSet.hasProperty( COMPONENTS ) )
        {
            builder.page( pageDataSerializer.fromData( contentAsSet ) );
        }
    }

    private void extractExtradata( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final ExtraDatas extraData = extraDataSerializer.fromData( contentAsSet.getSet( EXTRA_DATA ) );

        if ( extraData != null && extraData.isNotEmpty() )
        {
            builder.extraDatas( extraData );
        }
    }

    private void extractLanguage( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        String language = contentAsSet.getString( LANGUAGE );
        if ( !isNullOrEmpty( language ) )
        {
            builder.language( Locale.forLanguageTag( language ) );
        }
    }

    private void extractOwner( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        String owner = contentAsSet.getString( OWNER );

        if ( !nullToEmpty( owner ).isBlank() )
        {
            builder.owner( PrincipalKey.from( owner ) );
        }
    }

    private void extractProcessedReferences( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        Iterable<Reference> references = contentAsSet.getReferences( PROCESSED_REFERENCES );

        references.forEach( reference -> builder.addProcessedReference( ContentId.from( reference ) ) );
    }

    private void extractWorkflowInfo( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final PropertySet workflowInfoSet = contentAsSet.getSet( WORKFLOW_INFO );
        final WorkflowInfo workflowInfo = workflowInfoSerializer.extract( workflowInfoSet );
        builder.workflowInfo( workflowInfo );
    }

    private void extractInherit( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        builder.setInherit( StreamSupport.stream( contentAsSet.getStrings( INHERIT ).spliterator(), false )
                                .map( ContentInheritType::valueOf )
                                .collect( Collectors.toSet() ) );
    }

    private void extractValidationErrors( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        builder.validationErrors( ValidationErrors.create()
                                      .addAll( StreamSupport.stream( contentAsSet.getSets( VALIDATION_ERRORS ).spliterator(), false )
                                                   .map( this::mapValidationError )
                                                   .collect( Collectors.toList() ) )
                                      .build() );
    }

    private ValidationError mapValidationError( final PropertySet ve )
    {
        final Object[] args = Optional.ofNullable( ve.getString( "args" ) ).map( argsJson -> {
            try
            {
                return OBJECT_MAPPER.readValue( argsJson, Object[].class );
            }
            catch ( JsonProcessingException e )
            {
                throw new UncheckedIOException( e );
            }
        } ).orElse( null );

        final ValidationErrorCode errorCode = ValidationErrorCode.parse( ve.getString( "errorCode" ) );

        if ( ve.hasProperty( "propertyPath" ) )
        {
            return ValidationError.dataError( errorCode, PropertyPath.from( ve.getString( "propertyPath" ) ) )
                .message( ve.getString( "message" ), true )
                .i18n( ve.getString( "i18n" ) )
                .args( args )
                .build();
        }
        else if ( ve.hasProperty( "attachment" ) )
        {
            return ValidationError.attachmentError( errorCode, BinaryReference.from( ve.getString( "attachment" ) ) )
                .message( ve.getString( "message" ), true )
                .i18n( ve.getString( "i18n" ) )
                .args( args )
                .build();
        }
        else
        {
            return ValidationError.generalError( errorCode )
                .message( ve.getString( "message" ), true )
                .i18n( ve.getString( "i18n" ) )
                .args( args )
                .build();
        }
    }

    private void extractOriginProject( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final String originProject = contentAsSet.getString( ORIGIN_PROJECT );
        if ( !isNullOrEmpty( originProject ) )
        {
            builder.originProject( ProjectName.from( originProject ) );
        }
    }

    private void extractOriginalName( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final String originalName = contentAsSet.getString( ORIGINAL_NAME );
        if ( !isNullOrEmpty( originalName ) )
        {
            builder.originalName( ContentName.from( originalName ) );
        }
    }

    private void extractOriginalParentPath( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final String originalParentPath = contentAsSet.getString( ORIGINAL_PARENT_PATH );
        if ( !isNullOrEmpty( originalParentPath ) )
        {
            builder.originalParentPath( ContentPath.from( originalParentPath ) );
        }
    }

    private void extractArchivedTime( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        builder.archivedTime( contentAsSet.getInstant( ARCHIVED_TIME ) );
    }

    private void extractArchivedBy( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        String archivedBy = contentAsSet.getString( ARCHIVED_BY );

        if ( !nullToEmpty( archivedBy ).isBlank() )
        {
            builder.archivedBy( PrincipalKey.from( archivedBy ) );
        }
    }

    private void extractVariantOf( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final Reference variantOfRef = contentAsSet.getReference( VARIANT_OF );
        if ( variantOfRef != null )
        {
            builder.variantOf( ContentId.from( variantOfRef.getNodeId() ) );
        }
    }

    private Attachments dataToAttachments( final Iterable<PropertySet> attachmentSets )
    {
        final Attachments.Builder attachments = Attachments.create();
        for ( final PropertySet attachmentAsSet : attachmentSets )
        {
            attachments.add( Attachment.create()
                                 .name( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_NAME ) )
                                 .label( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_LABEL ) )
                                 .mimeType( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) )
                                 .size( attachmentAsSet.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) )
                                 .sha512( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_SHA512 ) )
                                 .textContent( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_TEXT ) )
                                 .build() );
        }
        return attachments.build();
    }

    private void applyAttachmentsAsData( final Content content, final PropertySet contentAsData )
    {
        for ( final Attachment attachment : content.getAttachments() )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ATTACHMENT );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, attachment.getName() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_LABEL, attachment.getLabel() );
            attachmentSet.addBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF, attachment.getBinaryReference() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_MIMETYPE, attachment.getMimeType() );
            attachmentSet.addLong( ContentPropertyNames.ATTACHMENT_SIZE, attachment.getSize() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_SHA512, attachment.getSha512() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_TEXT, attachment.getTextContent() );
        }
    }

    private void addValidationErrors( final ValidationErrors validationErrors, final PropertySet contentAsData )
    {
        if ( validationErrors != null && validationErrors.hasErrors() )
        {
            contentAsData.addSets( VALIDATION_ERRORS, validationErrors.stream().map( validationError -> {
                final PropertySet propertySet = contentAsData.getTree().newSet();
                propertySet.addString( "errorCode", validationError.getErrorCode().toString() );
                propertySet.addString( "message", validationError.getMessage() );
                propertySet.addString( "i18n", validationError.getI18n() );
                if ( !validationError.getArgs().isEmpty() )
                {
                    try
                    {
                        propertySet.addString( "args", OBJECT_MAPPER.writeValueAsString( validationError.getArgs() ) );
                    }
                    catch ( JsonProcessingException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                }

                if ( validationError instanceof DataValidationError )
                {
                    propertySet.addString( "propertyPath", ( (DataValidationError) validationError ).getPropertyPath().toString() );
                }
                else if ( validationError instanceof AttachmentValidationError )
                {
                    propertySet.addString( "attachment", ( (AttachmentValidationError) validationError ).getAttachment().toString() );
                }
                return propertySet;
            } ).toArray( PropertySet[]::new ) );
        }
    }
}
