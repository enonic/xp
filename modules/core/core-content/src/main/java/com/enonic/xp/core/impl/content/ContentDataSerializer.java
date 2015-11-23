package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentTranslatorParams;
import com.enonic.xp.core.impl.content.page.PageDataSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Exceptions;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.DISPLAY_NAME;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.LANGUAGE;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.content.ContentPropertyNames.VALID;

public final class ContentDataSerializer
{
    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE );

    public PropertyTree toNodeData( final UpdateContentTranslatorParams params )
    {
        final PropertyTree newPropertyTree = new PropertyTree();
        final PropertySet contentAsData = newPropertyTree.getRoot();

        final Content content = params.getEditedContent();
        final CreateAttachments createAttachments = params.getCreateAttachments();

        contentAsData.setBoolean( ContentPropertyNames.VALID, content.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, content.getType().toString() );
        contentAsData.ifNotNull().addString( OWNER, content.getOwner() != null ? content.getOwner().toString() : null );
        contentAsData.ifNotNull().addString( LANGUAGE, content.getLanguage() != null ? content.getLanguage().toLanguageTag() : null );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, content.getModifiedTime() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addString( CREATOR, content.getCreator().toString() );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, content.getCreatedTime() );
        contentAsData.addSet( DATA, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasExtraData() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.EXTRA_DATA );

            for ( final ExtraData extraData : content.getAllExtraData() )
            {

                final String xDataApplicationPrefix = extraData.getApplicationPrefix();
                PropertySet xDataApplication = metadataSet.getSet( xDataApplicationPrefix );
                if ( xDataApplication == null )

                {
                    xDataApplication = metadataSet.addSet( xDataApplicationPrefix );
                }
                xDataApplication.addSet( extraData.getName().getLocalName(),
                                         extraData.getData().getRoot().copy( contentAsData.getTree() ) );
            }
        }

        if ( createAttachments != null )
        {
            applyCreateAttachments( createAttachments, contentAsData );
        }
        else
        {
            applyAttachmentsAsData( content.getAttachments(), contentAsData );
        }

        if ( content.hasPage() )
        {
            PAGE_SERIALIZER.toData( content.getPage(), contentAsData );
        }

        return newPropertyTree;
    }

    void toCreateNodeData( final CreateContentTranslatorParams params, final PropertySet contentAsData )
    {
        contentAsData.addBoolean( VALID, params.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, params.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, params.getType() != null ? params.getType().toString() : null );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, params.getCreatedTime() );
        contentAsData.ifNotNull().addString( CREATOR, params.getCreator().toString() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, params.getModifiedTime() );
        contentAsData.ifNotNull().addString( OWNER, PrincipalKey.ofAnonymous().equals( params.getOwner() ) || params.getOwner() == null
            ? null
            : params.getOwner().toString() );
        contentAsData.ifNotNull().addString( LANGUAGE, params.getLanguage() != null ? params.getLanguage().toLanguageTag() : null );
        contentAsData.addSet( DATA, params.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( params.getExtraDatas() != null && !params.getExtraDatas().isEmpty() )
        {
            final PropertySet metaSet = contentAsData.addSet( EXTRA_DATA );
            for ( final ExtraData extraData : params.getExtraDatas() )
            {

                final String xDataApplicationPrefix = extraData.getApplicationPrefix();
                PropertySet xDataApplication = metaSet.getSet( xDataApplicationPrefix );
                if ( xDataApplication == null )
                {
                    xDataApplication = metaSet.addSet( xDataApplicationPrefix );
                }
                xDataApplication.addSet( extraData.getName().getLocalName(), extraData.getData().getRoot().copy( metaSet.getTree() ) );
            }
        }

        if ( params.getCreateAttachments() != null )
        {
            applyCreateAttachments( params.getCreateAttachments(), contentAsData );
        }
    }

    public Content.Builder fromData( final PropertySet contentAsSet )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( contentAsSet.getString( ContentPropertyNames.TYPE ) );
        final Content.Builder builder = Content.create( contentTypeName );

        builder.displayName( contentAsSet.getString( DISPLAY_NAME ) );
        builder.valid( contentAsSet.getBoolean( VALID ) != null ? contentAsSet.getBoolean( ContentPropertyNames.VALID ) : false );
        builder.data( contentAsSet.getSet( DATA ).toTree() );

        addUserInfo( contentAsSet, builder );
        addOwner( contentAsSet, builder );
        addLanguage( contentAsSet, builder );
        addExtraData( contentAsSet, builder );
        addPage( contentAsSet, builder );
        addAttachments( contentAsSet, builder );

        return builder;
    }

    private void addUserInfo( final PropertySet contentAsSet, final Content.Builder builder )
    {
        builder.creator( PrincipalKey.from( contentAsSet.getString( CREATOR ) ) );
        builder.createdTime( contentAsSet.getInstant( CREATED_TIME ) );
        builder.modifier( contentAsSet.getString( MODIFIER ) != null ? PrincipalKey.from( contentAsSet.getString( MODIFIER ) ) : null );
        builder.modifiedTime( contentAsSet.getInstant( MODIFIED_TIME ) != null ? contentAsSet.getInstant( MODIFIED_TIME ) : null );
    }

    private void addAttachments( final PropertySet contentAsSet, final Content.Builder builder )
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

    private void addPage( final PropertySet contentAsSet, final Content.Builder builder )
    {
        if ( contentAsSet.hasProperty( PAGE ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( contentAsSet.getSet( PAGE ) ) );
        }
    }

    private void addExtraData( final PropertySet contentAsSet, final Content.Builder builder )
    {
        final PropertySet metadataSet = contentAsSet.getSet( EXTRA_DATA );
        if ( metadataSet != null )
        {
            final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
            for ( final String metadataApplicationPrefix : metadataSet.getPropertyNames() )
            {
                final PropertySet xDataApplication = metadataSet.getSet( metadataApplicationPrefix );
                for ( final String metadataLocalName : xDataApplication.getPropertyNames() )
                {

                    final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( metadataApplicationPrefix );

                    final MixinName metadataName = MixinName.from( applicationKey, metadataLocalName );
                    extradatasBuilder.add( new ExtraData( metadataName, xDataApplication.getSet( metadataLocalName ).toTree() ) );
                }
            }

            builder.extraDatas( extradatasBuilder.build() );
        }
    }

    private void addLanguage( final PropertySet contentAsSet, final Content.Builder builder )
    {
        String language = contentAsSet.getString( LANGUAGE );
        if ( StringUtils.isNotEmpty( language ) )
        {
            builder.language( Locale.forLanguageTag( language ) );
        }
    }

    private void addOwner( final PropertySet contentAsSet, final Content.Builder builder )
    {
        String owner = contentAsSet.getString( OWNER );

        if ( StringUtils.isNotBlank( owner ) )
        {
            builder.owner( PrincipalKey.from( owner ) );
        }
    }

    Attachments dataToAttachments( final Iterable<PropertySet> attachmentSets )
    {
        final Attachments.Builder attachments = Attachments.create();
        for ( final PropertySet attachmentAsSet : attachmentSets )
        {
            attachments.add( Attachment.create().
                name( attachmentAsSet.getString( "name" ) ).
                label( attachmentAsSet.getString( "label" ) ).
                mimeType( attachmentAsSet.getString( "mimeType" ) ).
                size( attachmentAsSet.getLong( "size" ) ).
                build() );
        }
        return attachments.build();
    }

    private void applyCreateAttachments( final CreateAttachments createAttachments, final PropertySet contentAsData )
    {
        for ( final CreateAttachment createAttachment : createAttachments )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ATTACHMENT );
            attachmentSet.addString( "name", createAttachment.getName() );
            attachmentSet.addString( "label", createAttachment.getLabel() );
            attachmentSet.addBinaryReference( "binary", createAttachment.getBinaryReference() );
            attachmentSet.addString( "mimeType", createAttachment.getMimeType() );

            try
            {
                attachmentSet.addLong( "size", createAttachment.getByteSource().size() );
            }
            catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }
        }
    }

    private void applyAttachmentsAsData( final Attachments attachments, final PropertySet contentAsData )
    {
        for ( final Attachment attachment : attachments )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ATTACHMENT );
            attachmentSet.addString( "name", attachment.getName() );
            attachmentSet.addString( "label", attachment.getLabel() );
            attachmentSet.addBinaryReference( "binary", attachment.getBinaryReference() );
            attachmentSet.addString( "mimeType", attachment.getMimeType() );
            attachmentSet.addLong( "size", attachment.getSize() );
        }
    }
}
