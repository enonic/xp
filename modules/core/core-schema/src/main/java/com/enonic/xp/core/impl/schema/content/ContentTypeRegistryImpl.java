package com.enonic.xp.core.impl.schema.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeRegistry;
import com.enonic.xp.schema.content.ContentTypes;

@Component(immediate = true)
public final class ContentTypeRegistryImpl
    implements ContentTypeRegistry, ApplicationInvalidator
{
    private final BuiltinContentTypes builtInTypes;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private final Map<ContentTypeName, ContentType> map;

    public ContentTypeRegistryImpl()
    {
        this.map = Maps.newConcurrentMap();
        this.builtInTypes = new BuiltinContentTypes();
    }

    private boolean isSystem( final ContentTypeName name )
    {
        return isSystem( name.getApplicationKey() );
    }

    private boolean isSystem( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key );
    }

    @Override
    public ContentType get( final ContentTypeName name )
    {
        return this.map.computeIfAbsent( name, this::load );
    }

    private ContentType load( final ContentTypeName name )
    {
        if ( isSystem( name ) )
        {
            return this.builtInTypes.getAll().getContentType( name );
        }

        return new ContentTypeLoader( this.resourceService ).load( name );
    }

    @Override
    public ContentTypes getByApplication( final ApplicationKey key )
    {
        if ( isSystem( key ) )
        {
            return this.builtInTypes.getByApplication( key );
        }

        final List<ContentType> list = Lists.newArrayList();
        for ( final ContentTypeName name : findNames( key ) )
        {
            final ContentType type = get( name );
            if ( type != null )
            {
                list.add( type );
            }

        }

        return ContentTypes.from( list );
    }

    private List<ContentTypeName> findNames( final ApplicationKey key )
    {
        return new ContentTypeLoader( this.resourceService ).findNames( key );
    }

    @Override
    public ContentTypes getAll()
    {
        final Set<ContentType> contentTypeList = Sets.newLinkedHashSet();

        //Gets builtin content types
        for ( final ApplicationKey systemReservedApplicationKey : ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS )
        {
            final ContentTypes contentTypes = getByApplication( systemReservedApplicationKey );
            contentTypeList.addAll( contentTypes.getList() );
        }

        //Gets application content types
        for ( final Application application : this.applicationService.getAllApplications() )
        {
            final ContentTypes contentTypes = getByApplication( application.getKey() );
            contentTypeList.addAll( contentTypes.getList() );
        }

        return ContentTypes.from( contentTypeList );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.map.clear();
    }
}
