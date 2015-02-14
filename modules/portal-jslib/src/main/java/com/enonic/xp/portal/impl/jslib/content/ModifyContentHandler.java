package com.enonic.xp.portal.impl.jslib.content;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentEditor;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.EditableContent;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.convert.Converters;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.portal.impl.jslib.mapper.ContentMapper;

@Component(immediate = true)
public final class ModifyContentHandler
    implements CommandHandler
{
    private ContentService contentService;

    private MixinService mixinService;

    @Override
    public String getName()
    {
        return "content.modify";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String key = req.param( "key" ).required().value( String.class );
        final Function<Object[], Object> editor = req.param( "editor" ).required().callback();

        final ContentId id = findContentId( key );
        if ( id == null )
        {
            return null;
        }

        final UpdateContentParams params = new UpdateContentParams();
        params.contentId( id );
        params.editor( newContentEditor( editor ) );

        final Content result = this.contentService.update( params );
        return result != null ? new ContentMapper( result ) : null;
    }

    private ContentId findContentId( final String key )
    {
        if ( !key.startsWith( "/" ) )
        {
            return ContentId.from( key );
        }

        try
        {
            final Content content = this.contentService.getByPath( ContentPath.from( key ) );
            return content.getId();
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private ContentEditor newContentEditor( final Function<Object[], Object> func )
    {
        return edit -> {
            final Object value = func.apply( new Object[]{new ContentMapper( edit.source )} );
            if ( value instanceof Map )
            {
                updateContent( edit, (Map) value );
            }
        };
    }

    private void updateContent( final EditableContent target, final Map<?, ?> map )
    {
        final String displayName = Converters.convert( map.get( "displayName" ), String.class );
        if ( displayName != null )
        {
            target.displayName = displayName;
        }

        final Boolean draft = Converters.convert( map.get( "draft" ), Boolean.class );
        if ( draft != null )
        {
            target.validated = draft;
        }

        final Object data = map.get( "data" );
        if ( data instanceof Map )
        {
            target.data = propertyTree( (Map) data );
        }

        final Object metadata = map.get( "x" );
        if ( metadata instanceof Map )
        {
            target.metadata = metaDatas( (Map) metadata );
        }
    }

    private PropertyTree propertyTree( final Map<?, ?> value )
    {
        if ( value == null )
        {
            return null;
        }

        final PropertyTree tree = new PropertyTree();
        applyData( tree.getRoot(), value );
        return tree;
    }

    private void applyData( final PropertySet set, final Map<?, ?> value )
    {
        for ( final Map.Entry<?, ?> entry : value.entrySet() )
        {
            final String name = entry.getKey().toString();
            final Object item = entry.getValue();

            if ( item instanceof Map )
            {
                applyData( set.addSet( name ), (Map) item );
            }
            else if ( item instanceof Iterable )
            {
                applyData( set, name, (Iterable<?>) item );
            }
            else if ( item instanceof Double )
            {
                set.addDouble( name, (Double) item );
            }
            else if ( item instanceof Number )
            {
                set.addLong( name, ( (Number) item ).longValue() );
            }
            else if ( item instanceof Boolean )
            {
                set.addBoolean( name, (Boolean) item );
            }
            else
            {
                set.addString( name, item.toString() );
            }
        }
    }

    private void applyData( final PropertySet set, final String name, final Object value )
    {
        if ( value instanceof Map )
        {
            applyData( set.addSet( name ), (Map) value );
        }
        else if ( value instanceof Iterable )
        {
            applyData( set, name, (Iterable<?>) value );
        }
        else
        {
            set.addString( name, value.toString() );
        }
    }

    private void applyData( final PropertySet set, final String name, final Iterable<?> value )
    {
        for ( final Object item : value )
        {
            applyData( set, name, item );
        }
    }

    private Metadatas metaDatas( final Map<?, ?> value )
    {
        return Metadatas.from( metaDataList( value ) );
    }

    private List<Metadata> metaDataList( final Map<?, ?> value )
    {
        if ( value == null )
        {
            return null;
        }

        final List<Metadata> list = Lists.newArrayList();
        for ( final Map.Entry<?, ?> entry : value.entrySet() )
        {
            final Metadata item = metaData( entry.getKey().toString(), entry.getValue() );
            if ( item != null )
            {
                list.add( item );
            }
        }

        return list;
    }

    private Metadata metaData( final String localName, final Object value )
    {
        if ( value instanceof Map )
        {
            final Mixin mixin = mixinService.getByLocalName( localName );
            if ( mixin != null )
            {
                return new Metadata( mixin.getName(), propertyTree( (Map) value ) );
            }
        }

        return null;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
