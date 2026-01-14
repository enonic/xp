package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
        edit( map, "displayName", String.class, Function.identity(), target.displayName );
        edit( map, "language", String.class, Locale::forLanguageTag, target.language );
        edit( map, "childOrder", String.class, ChildOrder::from, target.childOrder );
        edit( map, "owner", String.class, PrincipalKey::from, target.owner );
        edit( map, "creator", String.class, PrincipalKey::from, target.creator );
        edit( map, "createdTime", String.class, Instant::parse, target.createdTime );
        edit( map, "modifier", String.class, PrincipalKey::from, target.modifier );
        edit( map, "modifiedTime", String.class, Instant::parse, target.modifiedTime );
        edit( map, "data", Map.class, v -> createPropertyTree( v, target.type.originalValue ), target.data );
        edit( map, "x", Map.class, v -> createExtraDatas( v, target.type.originalValue ), target.extraDatas );
        edit( map, "publish", Map.class, this::createContentPublishInfo, target.publishInfo );
        edit( map, "workflow", Map.class, this::createWorkflowInfo, target.workflowInfo );
        edit( map, "page", Map.class, this::createPage, target.page );
        edit( map, "validationErrors", List.class, this::createValidationErrors, target.validationErrors );
        edit( map, "valid", Boolean.class, Function.identity(), target.valid );
        edit( map, "processedReferences", String[].class, ContentIds::from, target.processedReferences );
        edit( map, "manualOrderValue", Long.class, Function.identity(), target.manualOrderValue );
        edit( map, "inherit", String[].class, this::createInherit, target.inherit );
        edit( map, "variantOf", String.class, ContentId::from, target.variantOf );
        edit( map, "originProject", String.class, ProjectName::from, target.originProject );
        edit( map, "originalParentPath", String.class, ContentPath::from, target.originalParentPath );
        edit( map, "originalName", String.class, ContentName::from, target.originalName );
        edit( map, "archivedTime", String.class, Instant::parse, target.archivedTime );
        edit( map, "archivedBy", String.class, PrincipalKey::from, target.archivedBy );
    }

    private <T, R> void edit( Map<String, ?> map, String key, Class<T> type, Function<T, R> mapper,
                              PatchableContent.PatchableField<R> field )
    {
        edit( map, key, type, v -> v.map( mapper ).ifPresentOrElse( field::setValue, field::remove ) );
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

    private Set<ContentInheritType> createInherit( final String[] list )
    {
        return Arrays.stream( list ).map( ContentInheritType::valueOf ).collect( Collectors.toSet() );
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
