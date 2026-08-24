package com.enonic.xp.lib.content;

import java.util.List;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.content.mapper.SortContentResultMapper;
import com.enonic.xp.script.ScriptValue;

public final class SortContentHandler
    extends BaseContextHandler
{
    private String key;

    private String childOrder;

    private ScriptValue reorder;

    @Override
    protected Object doExecute()
    {
        final ContentId contentId = getContentId( this.key );

        final SortContentParams.Builder builder = SortContentParams.create().contentId( contentId );

        final List<ReorderChildContentParams> reorderParams = parseReorder();

        final ChildOrder order = resolveChildOrder( reorderParams );

        builder.childOrder( order );
        reorderParams.forEach( builder::addManualOrder );

        return new SortContentResultMapper( contentService.sort( builder.build() ) );
    }

    private ChildOrder resolveChildOrder( final List<ReorderChildContentParams> reorderParams )
    {
        if ( "manual".equals( this.childOrder ) )
        {
            return ChildOrder.orderKeyOrder();
        }
        if ( this.childOrder != null )
        {
            return ChildOrder.from( this.childOrder );
        }
        if ( !reorderParams.isEmpty() )
        {
            // reordering only makes sense under manual ordering, so flip to it
            return ChildOrder.orderKeyOrder();
        }
        throw new IllegalArgumentException( "Either 'childOrder' or 'reorder' is required" );
    }

    private List<ReorderChildContentParams> parseReorder()
    {
        if ( this.reorder == null )
        {
            return List.of();
        }
        return this.reorder.getArray().stream().map( this::convertToReorderParams ).toList();
    }

    private ReorderChildContentParams convertToReorderParams( final ScriptValue entry )
    {
        final ScriptValue contentId = entry.getMember( "contentId" );
        if ( contentId == null )
        {
            throw new IllegalArgumentException( "Parameter 'contentId' is required in reorder entries" );
        }
        return ReorderChildContentParams.create()
            .contentToMove( ContentId.from( contentId.getValue( String.class ) ) )
            .afterOrderKey( getStringMember( entry, "afterOrderKey" ) )
            .beforeOrderKey( getStringMember( entry, "beforeOrderKey" ) )
            .build();
    }

    private static String getStringMember( final ScriptValue entry, final String name )
    {
        final ScriptValue member = entry.getMember( name );
        return member != null ? member.getValue( String.class ) : null;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setChildOrder( final String childOrder )
    {
        this.childOrder = childOrder;
    }

    public void setReorder( final ScriptValue reorder )
    {
        this.reorder = reorder;
    }
}
