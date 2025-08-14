package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import com.enonic.xp.convert.Converters;
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

    private String[] branches;

    @Override
    protected Object doExecute()
    {
        final Content existingContent = getExistingContent( this.key );
        if ( existingContent == null )
        {
            return null;
        }

        final PatchContentParams.Builder params =
            PatchContentParams.create().contentId( existingContent.getId() ).patcher( newContentPatcher( existingContent ) );

        if ( branches != null )
        {
            params.branches( Arrays.stream( branches ).map( Branch::from ).collect( Branches.collecting() ) );
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

    private ContentPatcher newContentPatcher( final Content existingContent )
    {
        return edit -> {
            final ScriptValue value = this.patcher.call( new ContentMapper( edit.source ) );
            if ( value != null )
            {
                patchContent( edit, value.getMap(), existingContent );
            }
        };
    }

    private void patchContent( final PatchableContent target, final Map<String, Object> map, final Content existingContent )
    {
        parse( map, "displayName", String.class, val -> target.displayName.setValue( val ) );
        parse( map, "language", String.class, val -> target.language.setValue( Locale.forLanguageTag( val ) ) );
        parse( map, "childOrder", String.class, val -> target.childOrder.setValue( ChildOrder.from( val ) ) );
        parse( map, "owner", String.class, val -> target.owner.setValue( PrincipalKey.from( val ) ) );
        parse( map, "creator", String.class, val -> target.creator.setValue( PrincipalKey.from( val ) ) );
        parse( map, "createdTime", String.class, val -> target.createdTime.setValue( Instant.parse( val ) ) );
        parse( map, "modifier", String.class, val -> target.modifier.setValue( PrincipalKey.from( val ) ) );
        parse( map, "modifiedTime", String.class, val -> target.modifiedTime.setValue( Instant.parse( val ) ) );

        parse( map, "data", Map.class, val -> target.data.setValue( createPropertyTree( val, existingContent.getType() ) ) );
        parse( map, "x", Map.class, val -> target.extraDatas.setValue( createExtraDatas( val, existingContent.getType() ) ) );
        parse( map, "publish", Map.class, val -> target.publishInfo.setValue( createContentPublishInfo( val ) ) );
        parse( map, "workflow", Map.class, val -> target.workflowInfo.setValue( createWorkflowInfo( val ) ) );

        parse( map, "page", Map.class, val -> target.page.setValue( createPage( val ) ) );
        parse( map, "validationErrors", List.class, val -> target.validationErrors.setValue( createValidationErrors( val ) ) );
//        parse(map, "attachments", Map.class, val -> target.attachments::setValue);

        parse( map, "valid", Boolean.class, target.valid::setValue );
        parse( map, "processedReferences", String[].class, val -> target.processedReferences.setValue(
            Arrays.stream( val ).map( ContentId::from ).collect( ContentIds.collector() ) ) );
        parse( map, "manualOrderValue", Long.class, target.manualOrderValue::setValue );
        parse( map, "inherit", List.class, ( List val ) -> {
            final Set collect = (Set) val.stream().map( str -> ContentInheritType.valueOf( str.toString() ) ).collect( Collectors.toSet() );
            target.inherit.setValue( collect );
        } );
        parse( map, "variantOf", ContentId.class, target.variantOf::setValue );

        parse( map, "originProject", String.class, val -> target.originProject.setValue( ProjectName.from( val ) ) );
        parse( map, "originalParentPath", String.class, val -> target.originalParentPath.setValue( ContentPath.from( val ) ) );
        parse( map, "originalName", String.class, val -> target.originalName.setValue( ContentName.from( val ) ) );
        parse( map, "archivedTime", String.class, val -> target.archivedTime.setValue( Instant.parse( val ) ) );
        parse( map, "archivedBy", String.class, val -> target.archivedBy.setValue( PrincipalKey.from( val ) ) );
    }

    private <T> void parse( Map<?, ?> map, String key, Class<T> type, Consumer<T> consumer )
    {
        T converted = Converters.convert( map.get( key ), type );
        if ( converted != null )
        {
            consumer.accept( converted );
        }
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

    public void setBranches( final String[] branches )
    {
        this.branches = branches;
    }
}
