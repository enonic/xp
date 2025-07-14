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
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.Page;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeName;
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

    public ModifiableField<ContentTypeName> type;

//    public ModifiableField<ContentPath> parentPath; TODO: verify

//    public ModifiableField<ContentName> name; TODO: verify

    public ModifiableField<ChildOrder> childOrder;

    public ModifiableField<ProjectName> originProject;

    public ModifiableField<ContentPath> originalParentPath;

    public ModifiableField<ContentName> originalName;

    public ModifiableField<Instant> archivedTime;

    public ModifiableField<PrincipalKey> archivedBy;

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
        this.type = new ModifiableField<>( source.getType() );
//        this.parentPath = new ModifiableField<>( source.getPath().getParentPath() );
//        this.name = new ModifiableField<>( source.getName() );
        this.childOrder = new ModifiableField<>( source.getChildOrder() );
        this.originProject = new ModifiableField<>( source.getOriginProject() );
        this.originalParentPath = new ModifiableField<>( source.getOriginalParentPath() );
        this.originalName = new ModifiableField<>( source.getOriginalName() );
        this.archivedTime = new ModifiableField<>( source.getArchivedTime() );
        this.archivedBy = new ModifiableField<>( source.getArchivedBy() );
    }

    public Content build()
    {
        return Content.create( this.source )
            .displayName( displayName.produce() )
            .data( data.produce() )
            .extraDatas( extraDatas.produce() )
            .page( page.produce() )
            .thumbnail( thumbnail.produce() )
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
//            .parentPath( parentPath.produce() )
//            .name( name.produce() )
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

    public class ModifiableField<T>
    {
        public T originalValue;

        private T producedValue;

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

        public T getProducedValue()
        {
            return this.producedValue;
        }

        T produce()
        {
            this.producedValue = switch ( policy )
            {
                case KEEP -> originalValue;
                case REPLACE -> modifier.apply( ModifiableContent.this );
                case REMOVE -> null;
            };

            return this.producedValue;
        }
    }
}
