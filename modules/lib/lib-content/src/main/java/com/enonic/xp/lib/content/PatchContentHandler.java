package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPatcher;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.lib.content.mapper.PatchContentResultMapper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class PatchContentHandler
    extends BaseContentHandler
{
    private String key;

    private ScriptValue patcher;

    private ScriptValue attachments;

    private List<String> branches;

    private boolean skipSync;

    @Override
    protected Object doExecute()
    {
        final Content existingContent = getExistingContent( this.key );
        if ( existingContent == null )
        {
            return null;
        }

        final PatchContentParams.Builder params = PatchContentParams.create()
            .contentId( existingContent.getId() )
            .patcher( newContentPatcher() )
            .createAttachments( parseCreateAttachments( (List) Optional.ofNullable( attachments )
                .map( att -> att.getMember( "createAttachments" ) )
                .map( ScriptValue::getList )
                .orElse( List.of() ) ) )
            .skipSync( this.skipSync );

        if ( branches != null )
        {
            params.branches( branches.stream().map( Branch::from ).collect( Branches.collector() ) );
        }

        final PatchContentResult result;
        try
        {
            result = this.contentService.patch( params.build() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        return new PatchContentResultMapper( result );
    }

    @Override
    protected boolean strictDataValidation()
    {
        return false;
    }

    private Content getExistingContent( final String key )
    {
        try
        {
            if ( !key.startsWith( "/" ) )
            {
                return this.contentService.getById( ContentId.from( key ) );
            }
            else
            {
                return this.contentService.getByPath( ContentPath.from( key ) );
            }
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private CreateAttachments parseCreateAttachments( List<Map<String, Object>> createAttachments )
    {
        if ( createAttachments == null || createAttachments.isEmpty() )
        {
            return CreateAttachments.empty();
        }

        return createAddAttachments( createAttachments );
    }

    private ContentPatcher newContentPatcher()
    {
        return edit -> {
            final ScriptValue value = this.patcher.call( new ContentMapper( edit.source ) );
            if ( value != null )
            {
                patchContent( edit, value.getMap() );
            }

            if ( this.attachments != null )
            {
                patchAttachments( edit, attachments.getMap() );
            }
        };
    }

    private void patchContent( final PatchableContent target, final Map<String, Object> map )
    {
        edit( map, "displayName", String.class, v -> target.displayName.setValue( v.orElse( null ) ) );
        edit( map, "language", String.class, v -> target.language.setValue( v.map( Locale::forLanguageTag ).orElse( null ) ) );
        edit( map, "childOrder", String.class, v -> target.childOrder.setValue( v.map( ChildOrder::from ).orElse( null ) ) );
        edit( map, "owner", String.class, v -> target.owner.setValue( v.map( PrincipalKey::from ).orElse( null ) ) );
        edit( map, "creator", String.class, v -> target.creator.setValue( v.map( PrincipalKey::from ).orElse( null ) ) );
        edit( map, "createdTime", String.class, v -> target.createdTime.setValue( v.map( Instant::parse ).orElse( null ) ) );
        edit( map, "modifier", String.class, v -> target.modifier.setValue( v.map( PrincipalKey::from ).orElse( null ) ) );
        edit( map, "modifiedTime", String.class, v -> target.modifiedTime.setValue( v.map( Instant::parse ).orElse( null ) ) );
        edit( map, "data", Map.class,
               v -> target.data.setValue( v.map( val -> createPropertyTree( val, target.type.originalValue ) ).orElse( null ) ) );
        edit( map, "x", Map.class,
               v -> target.extraDatas.setValue( v.map( val -> createExtraDatas( val, target.type.originalValue ) ).orElse( null ) ) );
        edit( map, "publish", Map.class, v -> target.publishInfo.setValue( v.map( this::createContentPublishInfo ).orElse( null ) ) );
        edit( map, "workflow", Map.class, v -> target.workflowInfo.setValue( v.map( this::createWorkflowInfo ).orElse( null ) ) );
        edit( map, "page", Map.class, v -> target.page.setValue( v.map( this::createPage ).orElse( null ) ) );
        edit( map, "validationErrors", List.class,
               v -> target.validationErrors.setValue( v.map( this::createValidationErrors ).orElse( null ) ) );
        edit( map, "valid", Boolean.class, v -> target.valid.setValue( v.orElse( null ) ) );
        edit( map, "processedReferences", String[].class,
               v -> target.processedReferences.setValue( v.map( ContentIds::from ).orElse( null ) ) );
        edit( map, "manualOrderValue", Long.class, v -> target.manualOrderValue.setValue( v.orElse( null ) ) );
        edit( map, "inherit", List.class, v -> target.inherit.setValue( v.map( this::createInherit ).orElse( null ) ) );
        edit( map, "variantOf", String.class, v -> target.variantOf.setValue( v.map( ContentId::from ).orElse( null ) ) );
        edit( map, "originProject", String.class, v -> target.originProject.setValue( v.map( ProjectName::from ).orElse( null ) ) );
        edit( map, "originalParentPath", String.class,
               v -> target.originalParentPath.setValue( v.map( ContentPath::from ).orElse( null ) ) );
        edit( map, "originalName", String.class, v -> target.originalName.setValue( v.map( ContentName::from ).orElse( null ) ) );
        edit( map, "archivedTime", String.class, v -> target.archivedTime.setValue( v.map( Instant::parse ).orElse( null ) ) );
        edit( map, "archivedBy", String.class, v -> target.archivedBy.setValue( v.map( PrincipalKey::from ).orElse( null ) ) );
    }

    private void patchAttachments( final PatchableContent target, final Map<String, Object> map )
    {
        final var modifyAttachments = (List<Map<String, Object>>) map.getOrDefault( "modifyAttachments", List.of() );
        final var removeAttachments = (List<String>) map.getOrDefault( "removeAttachments", List.of() );

        final Attachments parsedAttachments = createAttachments( modifyAttachments, target.attachments.originalValue ).stream()
            .filter( attachment -> !removeAttachments.contains( attachment.getName() ) )
            .collect( Attachments.collector() );

        final Set<String> originalNames =
            target.attachments.originalValue.stream().map( Attachment::getName ).collect( Collectors.toSet() );

        if ( parsedAttachments.stream().anyMatch( att -> !originalNames.contains( att.getName() ) ) )
        {
            throw new IllegalArgumentException( "Attachments can only be added using createAttachments" );
        }

        target.attachments.setValue( parsedAttachments );
    }

    private ContentPublishInfo createContentPublishInfo( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        return ContentPublishInfo.create()
            .from( getInstant( value, "from" ) )
            .to( getInstant( value, "to" ) )
            .first( getInstant( value, "first" ) )
            .build();
    }

    private Set<ContentInheritType> createInherit( final List<String> list )
    {
        return list.stream().map( ContentInheritType::valueOf ).collect( Collectors.toSet() );
    }

    private Instant getInstant( final Map<String, Object> valueMap, final String key )
    {
        final Object value = valueMap.get( key );
        if ( value != null )
        {
            try
            {
                return Instant.parse( value.toString() );
            }
            catch ( DateTimeParseException e )
            {
                throw new IllegalArgumentException( key + " value could not be parsed to instant: [" + value + "]" );
            }
        }
        return null;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setPatcher( final ScriptValue patcher )
    {
        this.patcher = patcher;
    }

    public void setBranches( final List<String> branches )
    {
        this.branches = branches;
    }

    public void setSkipSync( final boolean skipSync )
    {
        this.skipSync = skipSync;
    }

    public void setAttachments( final ScriptValue attachments )
    {
        this.attachments = attachments;
    }
}
