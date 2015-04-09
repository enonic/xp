package com.enonic.xp.portal.impl.jslib.content;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.impl.jslib.base.BaseContextHandler;
import com.enonic.xp.portal.impl.jslib.mapper.ContentMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true, service = CommandHandler.class)
public final class CreateContentHandler
    extends BaseContextHandler
{
    private ContentService contentService;

    private MixinService mixinService;

    @Override
    public String getName()
    {
        return "content.create";
    }

    @Override
    protected Object doExecute( final CommandRequest req )
    {
        final CreateContentParams params = createParams( req );
        final Content result = this.contentService.create( params );
        return new ContentMapper( result );
    }

    private CreateContentParams createParams( final CommandRequest req )
    {
        return CreateContentParams.create().
            name( req.param( "name" ).value( String.class ) ).
            parent( contentPath( req.param( "parentPath" ).value( String.class ) ) ).
            displayName( req.param( "displayName" ).value( String.class ) ).
            requireValid( req.param( "requireValid" ).value( Boolean.class, true ) ).
            type( contentTypeName( req.param( "contentType" ).value( String.class ) ) ).
            contentData( propertyTree( req.param( "data" ).map() ) ).
            extraDatas( extraDatas( req.param( "x" ).map() ) ).
            build();
    }

    private ContentPath contentPath( final String value )
    {
        return value != null ? ContentPath.from( value ) : null;
    }

    private ContentTypeName contentTypeName( final String value )
    {
        return value != null ? ContentTypeName.from( value ) : null;
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

    private ExtraDatas extraDatas( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        final ExtraDatas.Builder metadatasBuilder = ExtraDatas.builder();
        for ( final String modulePrefix : value.keySet() )
        {
            final ModuleKey moduleKey = ExtraData.fromModulePrefix( modulePrefix );
            final Object metadatasObject = value.get( modulePrefix );
            if ( !( metadatasObject instanceof Map ) )
            {
                continue;
            }

            final Map<?, ?> metadatas = (Map<?, ?>) metadatasObject;
            for ( final Map.Entry<?, ?> entry : metadatas.entrySet() )
            {
                final MixinName mixinName = MixinName.from( moduleKey, entry.getKey().toString() );
                final ExtraData item = metaData( mixinName, entry.getValue() );
                if ( item != null )
                {
                    metadatasBuilder.add( item );
                }
            }
        }

        return metadatasBuilder.build();
    }

    private ExtraData metaData( final MixinName mixinName, final Object value )
    {
        if ( value instanceof Map )
        {
            final Mixin mixin = mixinService.getByName( mixinName );
            if ( mixin != null )
            {
                return new ExtraData( mixin.getName(), propertyTree( (Map) value ) );
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
