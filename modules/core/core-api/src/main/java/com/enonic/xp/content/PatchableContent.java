package com.enonic.xp.content;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.Page;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public class PatchableContent
{
    public final Content source;

    public PatchableField<String> displayName;

    public PatchableField<PropertyTree> data;

    public PatchableField<ExtraDatas> extraDatas;

    public PatchableField<Page> page;

    public PatchableField<Boolean> valid;

    public PatchableField<PrincipalKey> owner;

    public PatchableField<Locale> language;

    public PatchableField<PrincipalKey> creator;

    public PatchableField<Instant> createdTime;

    public PatchableField<PrincipalKey> modifier;

    public PatchableField<Instant> modifiedTime;

    public PatchableField<ContentPublishInfo> publishInfo;

    public PatchableField<ContentIds> processedReferences;

    public PatchableField<WorkflowInfo> workflowInfo;

    public PatchableField<Long> manualOrderValue;

    public PatchableField<Set<ContentInheritType>> inherit;

    public PatchableField<ContentId> variantOf;

    public PatchableField<Attachments> attachments;

    public PatchableField<ValidationErrors> validationErrors;

    public PatchableField<ContentTypeName> type;

    public PatchableField<ChildOrder> childOrder;

    public PatchableField<ProjectName> originProject;

    public PatchableField<ContentPath> originalParentPath;

    public PatchableField<ContentName> originalName;

    public PatchableField<Instant> archivedTime;

    public PatchableField<PrincipalKey> archivedBy;

    public PatchableContent( final Content source )
    {
        this.source = source;
        this.displayName = new PatchableField<>( source.getDisplayName() );
        this.data = new PatchableField<>( source.getData().copy() );
        this.extraDatas = new PatchableField<>( source.getAllExtraData().copy() );
        this.page = new PatchableField<>( source.getPage() != null ? source.getPage().copy() : null );
        this.valid = new PatchableField<>( source.isValid() );
        this.owner = new PatchableField<>( source.getOwner() );
        this.language = new PatchableField<>( source.getLanguage() );
        this.creator = new PatchableField<>( source.getCreator() );
        this.createdTime = new PatchableField<>( source.getCreatedTime() );
        this.publishInfo = new PatchableField<>( source.getPublishInfo() );
        this.processedReferences = new PatchableField<>( source.getProcessedReferences() );
        this.workflowInfo = new PatchableField<>( source.getWorkflowInfo() );
        this.manualOrderValue = new PatchableField<>( source.getManualOrderValue() );
        this.inherit = new PatchableField<>(
            source.getInherit().isEmpty() ? EnumSet.noneOf( ContentInheritType.class ) : EnumSet.copyOf( source.getInherit() ) );
        this.variantOf = new PatchableField<>( source.getVariantOf() );

        // differs from "update"
        this.modifier = new PatchableField<>( source.getModifier() );
        this.modifiedTime = new PatchableField<>( source.getModifiedTime() );
        this.attachments = new PatchableField<>( source.getAttachments() );
        this.validationErrors = new PatchableField<>( source.getValidationErrors() );
        this.type = new PatchableField<>( source.getType() );
        this.childOrder = new PatchableField<>( source.getChildOrder() );
        this.originProject = new PatchableField<>( source.getOriginProject() );
        this.originalParentPath = new PatchableField<>( source.getOriginalParentPath() );
        this.originalName = new PatchableField<>( source.getOriginalName() );
        this.archivedTime = new PatchableField<>( source.getArchivedTime() );
        this.archivedBy = new PatchableField<>( source.getArchivedBy() );
    }

    public Content build()
    {
        return Content.create( this.source )
            .displayName( displayName.produce() )
            .data( data.produce() )
            .extraDatas( extraDatas.produce() )
            .page( page.produce() )
            .owner( owner.produce() )
            .language( language.produce() )
            .creator( creator.produce() )
            .createdTime( createdTime.produce() )
            .publishInfo( publishInfo.produce() )
            .processedReferences( processedReferences.produce() )
            .workflowInfo( workflowInfo.produce() )
            .manualOrderValue( manualOrderValue.produce() )
            .setInherit( inherit.produce() )
            .variantOf( variantOf.produce() )

            // differs from "update"
            .modifier( modifier.produce() )
            .modifiedTime( modifiedTime.produce() )
            .attachments( attachments.produce() )
            .type( type.produce() )
            .childOrder( childOrder.produce() )
            .originProject( originProject.produce() )
            .originalParentPath( originalParentPath.produce() )
            .originalName( originalName.produce() )
            .archivedTime( archivedTime.produce() )
            .archivedBy( archivedBy.produce() )
            .validationErrors( validationErrors.produce() )
            .valid( valid.produce() ) // should be after validationErrors
            .build();
    }

    private enum EditableFieldPolicy
    {
        KEEP, REPLACE, REMOVE
    }

    public class PatchableField<T>
    {
        public T originalValue;

        private T producedValue;

        private EditableFieldPolicy policy;

        private Function<PatchableContent, T> patcher;

        PatchableField( T value )
        {
            this.originalValue = value;
            this.policy = EditableFieldPolicy.KEEP;
        }

        public PatchableField<T> setPatcher( Function<PatchableContent, T> patcher )
        {
            this.patcher = patcher;
            this.policy = EditableFieldPolicy.REPLACE;
            return this;
        }

        public PatchableField<T> setValue( T value )
        {
            this.patcher = ( content ) -> value;
            this.policy = EditableFieldPolicy.REPLACE;
            return this;
        }

        public void remove()
        {
            this.policy = EditableFieldPolicy.REMOVE;
        }

        public T getProducedValue()
        {
            return this.producedValue;
        }

        T produce()
        {
            this.producedValue = switch ( policy )
            {
                case KEEP -> originalValue;
                case REPLACE -> patcher.apply( PatchableContent.this );
                case REMOVE -> null;
            };

            return this.producedValue;
        }
    }
}
