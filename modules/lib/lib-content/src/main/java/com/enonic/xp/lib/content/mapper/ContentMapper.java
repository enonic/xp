package com.enonic.xp.lib.content.mapper;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.page.Page;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.sortvalues.SortValuesProperty;

import static com.enonic.xp.lib.content.mapper.PageMapper.PAGE;

public final class ContentMapper
    implements MapSerializable
{
    private final Content value;

    private final SortValuesProperty sort;

    public ContentMapper( final Content value )
    {
        this( value, null );
    }

    public ContentMapper( final Content value, final SortValuesProperty sort )
    {
        this.value = value;
        this.sort = sort;
    }

    private void serialize( final MapGenerator gen, final Content value )
    {
        gen.value( "_id", value.getId() );
        gen.value( "_name", value.getName() );
        gen.value( "_path", value.getPath() );
        gen.value( "creator", value.getCreator() );
        gen.value( "modifier", value.getModifier() );
        gen.value( "createdTime", value.getCreatedTime() );
        gen.value( "modifiedTime", value.getModifiedTime() );
        gen.value( "owner", value.getOwner() );
        gen.value( "type", value.getType() );
        gen.value( "displayName", value.getDisplayName() );
        gen.value( "hasChildren", value.hasChildren() );
        gen.value( "language", value.getLanguage() );
        gen.value( "valid", value.isValid() );
        if ( value.getChildOrder() != null )
        {
            gen.value( "childOrder", value.getChildOrder().toString() );
        }
        if ( sort != null && sort.getValues() != null )
        {
            gen.array( "_sort" );
            for ( final Object sortValue : sort.getValues() )
            {
                gen.value( sortValue );
            }
            gen.end();
        }

        serializeData( gen, value.getData() );
        serializeExtraData( gen, value.getAllExtraData() );
        serializePage( gen, value.getPage() );
        serializeAttachments( gen, value.getAttachments() );
        serializePublishInfo( gen, value.getPublishInfo() );
        serializeWorkflowInfo( gen, value.getWorkflowInfo() );
    }

    private void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        gen.map( "data" );
        new PropertyTreeMapper( value ).serialize( gen );
        gen.end();
    }

    private void serializePublishInfo( final MapGenerator gen, final ContentPublishInfo info )
    {
        gen.map( "publish" );
        if ( info != null )
        {
            gen.value( "from", info.getFrom() );
            gen.value( "to", info.getTo() );
            gen.value( "first", info.getFirst() );
        }
        gen.end();
    }

    private void serializeWorkflowInfo( final MapGenerator gen, final WorkflowInfo info )
    {
        gen.map( "workflow" );
        if ( info != null )
        {
            gen.value( "state", info.getState().toString() );
            gen.map( "checks" );
            for ( Map.Entry<String, WorkflowCheckState> e : info.getChecks().entrySet() )
            {
                gen.value( e.getKey(), e.getValue().toString() );
            }
            gen.end();
        }
        gen.end();
    }

    private void serializeExtraData( final MapGenerator gen, final Iterable<ExtraData> values )
    {
        gen.map( "x" );

        final ListMultimap<ApplicationKey, ExtraData> extradatasByModule = ArrayListMultimap.create();
        for ( ExtraData extraData : values )
        {
            extradatasByModule.put( extraData.getName().getApplicationKey(), extraData );
        }

        for ( final ApplicationKey applicationKey : extradatasByModule.keys() )
        {
            final List<ExtraData> extraDatas = extradatasByModule.get( applicationKey );
            if ( extraDatas.isEmpty() )
            {
                continue;
            }
            gen.map( extraDatas.get( 0 ).getApplicationPrefix() );
            for ( final ExtraData extraData : extraDatas )
            {
                gen.map( extraData.getName().getLocalName() );
                new PropertyTreeMapper( extraData.getData() ).serialize( gen );
                gen.end();
            }
            gen.end();
        }
        gen.end();
    }

    private void serializePage( final MapGenerator gen, final Page value )
    {
        if ( value != null )
        {
            new PageMapper( value ).serialize( gen );
        }
        else
        {
            gen.map( PAGE );
            gen.end();
        }
    }

    private void serializeAttachments( final MapGenerator gen, final Attachments value )
    {
        gen.map( "attachments" );
        if ( value != null )
        {
            new AttachmentsMapper( value ).serialize( gen );
        }
        gen.end();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

