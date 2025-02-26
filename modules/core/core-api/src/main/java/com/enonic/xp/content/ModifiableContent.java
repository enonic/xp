package com.enonic.xp.content;


import java.time.Instant;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.page.Page;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public class ModifiableContent
{
    public final Content source;

    public ModifiableField<String> displayName;

    public ModifiableField<PropertyTree> data;

    public ModifiableField<ExtraDatas> extraDatas;

    public ModifiableField<Page> page;

    public ModifiableField<Boolean> valid;

    public ModifiableField<Thumbnail> thumbnail;

    public ModifiableField<PrincipalKey> owner;

    public ModifiableField<Locale> language;

    public ModifiableField<PrincipalKey> creator;

    public ModifiableField<Instant> createdTime;

    public ModifiableField<PrincipalKey> modifier;

    public ModifiableField<Instant> modifiedTime;

    public ModifiableField<ContentPublishInfo> publishInfo;

    public ModifiableField<ContentIds> processedReferences;

    public ModifiableField<WorkflowInfo> workflowInfo;

    public ModifiableField<Long> manualOrderValue;

    public ModifiableField<Set<ContentInheritType>> inherit;

    public ModifiableField<ContentId> variantOf;

    public ModifiableField<Attachments> attachments;

    public ModifiableField<ValidationErrors> validationErrors;

    public ModifiableContent( final Content source )
    {
        this.source = source;
        this.displayName = new ModifiableField<>( source.getDisplayName() );
        this.data = new ModifiableField<>( source.getData().copy() );
        this.extraDatas = new ModifiableField<>( source.getAllExtraData().copy() );
        this.page = new ModifiableField<>( source.getPage() != null ? source.getPage().copy() : null );
        this.valid = new ModifiableField<>( source.isValid() );
        this.thumbnail = new ModifiableField<>( source.getThumbnail() );
        this.owner = new ModifiableField<>( source.getOwner() );
        this.language = new ModifiableField<>( source.getLanguage() );
        this.creator = new ModifiableField<>( source.getCreator() );
        this.createdTime = new ModifiableField<>( source.getCreatedTime() );
        this.publishInfo = new ModifiableField<>( source.getPublishInfo() );
        this.processedReferences = new ModifiableField<>( source.getProcessedReferences() );
        this.workflowInfo = new ModifiableField<>( source.getWorkflowInfo() );
        this.manualOrderValue = new ModifiableField<>( source.getManualOrderValue() );
        this.inherit = new ModifiableField<>(
            source.getInherit().isEmpty() ? EnumSet.noneOf( ContentInheritType.class ) : EnumSet.copyOf( source.getInherit() ) );
        this.variantOf = new ModifiableField<>( source.getVariantOf() );

        // differs from "update"
        this.modifier = new ModifiableField<>( source.getModifier() );
        this.modifiedTime = new ModifiableField<>( source.getModifiedTime() );
        this.attachments = new ModifiableField<>( source.getAttachments() );

        this.validationErrors = new ModifiableField<>( source.getValidationErrors() );
    }

    public Content build()
    {
        return Content.create( this.source )
            .displayName( displayName.produce() )
            .data( data.produce() )
            .extraDatas( extraDatas.produce() )
            .page( page.produce() )
            .valid( valid.produce() )
            .thumbnail( thumbnail.produce() )
            .owner( owner.produce() )
            .language( language.produce() )
            .creator( creator.produce() )
            .createdTime( createdTime.produce() )
            .publishInfo( publishInfo.produce() ).processedReferences( processedReferences.produce() )
            .workflowInfo( workflowInfo.produce() )
            .manualOrderValue( manualOrderValue.produce() )
            .setInherit( inherit.produce() )
            .variantOf( variantOf.produce() )

            // differs from "update"
            .modifier( modifier.produce() )
            .modifiedTime( modifiedTime.produce() )
            .attachments( attachments.produce() ).validationErrors( validationErrors.produce() )
            .build();
    }

    private enum EditableFieldPolicy
    {
        KEEP, REPLACE, REMOVE
    }

    public class ModifiableField<T>
    {
        public T originalValue;

        private EditableFieldPolicy policy;

        private Function<ModifiableContent, T> modifier;

        ModifiableField( T value )
        {
            this.originalValue = value;
            this.policy = EditableFieldPolicy.KEEP;
        }

        public ModifiableField<T> setModifier( Function<ModifiableContent, T> modifier )
        {
            this.modifier = modifier;
            this.policy = EditableFieldPolicy.REPLACE;
            return this;
        }

        public ModifiableField<T> setValue( T value )
        {
            this.modifier = ( content ) -> value;
            this.policy = EditableFieldPolicy.REPLACE;
            return this;
        }

        public void remove()
        {
            this.policy = EditableFieldPolicy.REMOVE;
        }

        T produce()
        {
            return switch ( policy )
            {
                case KEEP -> originalValue;
                case REPLACE -> modifier.apply( ModifiableContent.this );
                case REMOVE -> null;
            };
        }
    }
}
