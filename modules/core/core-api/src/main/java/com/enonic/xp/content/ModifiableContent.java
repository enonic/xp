package com.enonic.xp.content;


import java.time.Instant;
import java.util.EnumSet;
import java.util.Locale;
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

    public EditableFieldPolicyWrapper<String> displayName;

    public EditableFieldPolicyWrapper<PropertyTree> data;

    public EditableFieldPolicyWrapper<ExtraDatas> extraDatas;

    public EditableFieldPolicyWrapper<Page> page;

    public EditableFieldPolicyWrapper<Boolean> valid;

    public EditableFieldPolicyWrapper<Thumbnail> thumbnail;

    public EditableFieldPolicyWrapper<PrincipalKey> owner;

    public EditableFieldPolicyWrapper<Locale> language;

    public EditableFieldPolicyWrapper<PrincipalKey> creator;

    public EditableFieldPolicyWrapper<Instant> createdTime;

    public EditableFieldPolicyWrapper<PrincipalKey> modifier;

    public EditableFieldPolicyWrapper<Instant> modifiedTime;

    public EditableFieldPolicyWrapper<ContentPublishInfo> publishInfo;

    public EditableFieldPolicyWrapper<ContentIds.Builder> processedReferences;

    public EditableFieldPolicyWrapper<WorkflowInfo> workflowInfo;

    public EditableFieldPolicyWrapper<Long> manualOrderValue;

    public EditableFieldPolicyWrapper<EnumSet<ContentInheritType>> inherit;

    public EditableFieldPolicyWrapper<ContentId> variantOf;

    public EditableFieldPolicyWrapper<Attachments> attachments;

    public ModifiableContent( final Content source )
    {
        this.source = source;
        this.displayName = new EditableFieldPolicyWrapper<>( source.getDisplayName() );
        this.data = new EditableFieldPolicyWrapper<>( source.getData().copy() );
        this.extraDatas = new EditableFieldPolicyWrapper<>( source.getAllExtraData().copy() );
        this.page = new EditableFieldPolicyWrapper<>( source.getPage() != null ? source.getPage().copy() : null );
        this.valid = new EditableFieldPolicyWrapper<>( source.isValid() );
        this.thumbnail = new EditableFieldPolicyWrapper<>( source.getThumbnail() );
        this.owner = new EditableFieldPolicyWrapper<>( source.getOwner() );
        this.language = new EditableFieldPolicyWrapper<>( source.getLanguage() );
        this.creator = new EditableFieldPolicyWrapper<>( source.getCreator() );
        this.createdTime = new EditableFieldPolicyWrapper<>( source.getCreatedTime() );
        this.publishInfo = new EditableFieldPolicyWrapper<>( source.getPublishInfo() );
        this.processedReferences = new EditableFieldPolicyWrapper<>( ContentIds.create().addAll( source.getProcessedReferences() ) );
        this.workflowInfo = new EditableFieldPolicyWrapper<>( source.getWorkflowInfo() );
        this.manualOrderValue = new EditableFieldPolicyWrapper<>( source.getManualOrderValue() );
        this.inherit = new EditableFieldPolicyWrapper<>(
            source.getInherit().isEmpty() ? EnumSet.noneOf( ContentInheritType.class ) : EnumSet.copyOf( source.getInherit() ) );
        this.variantOf = new EditableFieldPolicyWrapper<>( source.getVariantOf() );

        // differs from "update"
        this.modifier = new EditableFieldPolicyWrapper<>( source.getModifier() );
        this.modifiedTime = new EditableFieldPolicyWrapper<>( source.getModifiedTime() );
        this.attachments = new EditableFieldPolicyWrapper<>( source.getAttachments() );
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
            .publishInfo( publishInfo.produce() )
            .processedReferences( processedReferences.produce().build() )
            .workflowInfo( workflowInfo.produce() )
            .manualOrderValue( manualOrderValue.produce() )
            .setInherit( inherit.produce() )
            .variantOf( variantOf.produce() )

            // differs from "update"
            .modifier( modifier.produce() )
            .modifiedTime( modifiedTime.produce() )
            .attachments( attachments.produce() )
            .build();
    }

    private enum EditableFieldPolicy
    {
        KEEP, REPLACE, REMOVE
    }

    public static class EditableFieldPolicyWrapper<T>
    {
        public T originalValue;

        private EditableFieldPolicy policy;

        private Function<T, T> modifier = Function.identity();

        EditableFieldPolicyWrapper( T value )
        {
            this.originalValue = value;
            this.policy = EditableFieldPolicy.KEEP;
        }

        public EditableFieldPolicyWrapper<T> setModifier( Function<T, T> modifier )
        {
            this.modifier = modifier;
            this.policy = EditableFieldPolicy.REPLACE;
            return this;
        }

        public EditableFieldPolicyWrapper<T> setValue( T value )
        {
            this.modifier = ( v ) -> value;
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
                case REPLACE -> modifier.apply( originalValue );
                case REMOVE -> null;
            };
        }
    }
}
