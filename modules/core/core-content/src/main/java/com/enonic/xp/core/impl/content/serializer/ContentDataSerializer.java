package com.enonic.xp.core.impl.content.serializer;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.core.impl.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.page.Page;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
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
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_PUBLISHED;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_TO;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.content.ContentPropertyNames.VALID;
import static com.enonic.xp.content.ContentPropertyNames.VARIANT_OF;
import static com.enonic.xp.content.ContentPropertyNames.WORKFLOW_INFO;
import static com.enonic.xp.content.ContentPropertyNames.WORKFLOW_INFO_STATE;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.COMPONENTS;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

public final class ContentDataSerializer
{
    private final PageDataSerializer pageDataSerializer;

    private final ExtraDataSerializer extraDataSerializer;

    private final WorkflowInfoSerializer workflowInfoSerializer;

    private final ValidationErrorsSerializer validationErrorsSerializer;

    public ContentDataSerializer()
    {
        this( new PageDataSerializer() );
    }

    private ContentDataSerializer( final PageDataSerializer pageDataSerializer )
    {
        this.pageDataSerializer = pageDataSerializer;
        this.extraDataSerializer = new ExtraDataSerializer();
        this.workflowInfoSerializer = new WorkflowInfoSerializer();
        this.validationErrorsSerializer = new ValidationErrorsSerializer();
    }

    public PropertyTree toCreateNodeData( final CreateContentTranslatorParams params )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet contentAsData = propertyTree.getRoot();

        contentAsData.addBoolean( VALID, params.isValid() );

        validationErrorsSerializer.toData( params.getValidationErrors(), contentAsData );

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

        addWorkflowInfo( contentAsData, params.getWorkflowInfo() );
        if ( params.getPage() != null )
        {
            toPageData( params.getPage(), contentAsData );
        }

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
            toPageData( content.getPage(), contentAsData );
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
        validationErrorsSerializer.toData( content.getValidationErrors(), contentAsData );

        contentAsData.ifNotNull().addString( DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, content.getType().toString() );
        contentAsData.ifNotNull().addString( OWNER, content.getOwner() != null ? content.getOwner().toString() : null );
        contentAsData.ifNotNull().addString( LANGUAGE, content.getLanguage() != null ? content.getLanguage().toLanguageTag() : null );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, content.getModifiedTime() );
        contentAsData.ifNotNull().addString( MODIFIER, content.getModifier() != null ? content.getModifier().toString() : null );
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

    private static void addPublishInfo( final PropertySet contentAsData, final ContentPublishInfo data )
    {
        if ( data != null )
        {
            final PropertySet publishInfo = contentAsData.addSet( PUBLISH_INFO );
            publishInfo.setInstant( PUBLISH_FIRST, data.first() );
            publishInfo.setInstant( PUBLISH_FROM, data.from() );
            publishInfo.setInstant( PUBLISH_TO, data.to() );
            publishInfo.setInstant( PUBLISH_PUBLISHED, data.published() );
        }
    }

    public static void addWorkflowInfo( final PropertySet contentAsData, final WorkflowInfo data )
    {
        if ( data != null )
        {
            contentAsData.removeProperties( WORKFLOW_INFO );

            final PropertySet workflowInfo = contentAsData.addSet( WORKFLOW_INFO );
            workflowInfo.addString( WORKFLOW_INFO_STATE, data.getState().toString() );
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
        builder.publishInfo( PublishInfoSerializer.serialize( contentAsSet.getSet( PUBLISH_INFO ) ) );
    }

    private void extractAttachments( final PropertySet contentAsSet, final Content.Builder<?> builder )
    {
        final Attachments attachments = dataToAttachments( contentAsSet.getSets( ATTACHMENT ) );
        builder.attachments( attachments );
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

        if ( extraData != null && !extraData.isEmpty() )
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
        builder.validationErrors( validationErrorsSerializer.fromData( contentAsSet ) );
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
}
