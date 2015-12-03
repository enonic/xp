package com.enonic.xp.core.impl.schema.relationship;

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
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;

@Component(immediate = true)
public final class RelationshipTypeServiceImpl
    implements RelationshipTypeService, ApplicationInvalidator
{
    private final BuiltinRelationshipTypes builtInTypes;

    private final Map<RelationshipTypeName, RelationshipType> map;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    public RelationshipTypeServiceImpl()
    {
        this.map = Maps.newConcurrentMap();
        this.builtInTypes = new BuiltinRelationshipTypes();
    }

    @Override
    public RelationshipType getByName( final RelationshipTypeName name )
    {
        return this.map.computeIfAbsent( name, this::load );
    }

    private boolean isSystem( final RelationshipTypeName name )
    {
        return SchemaHelper.isSystem( name.getApplicationKey() );
    }

    private RelationshipType load( final RelationshipTypeName name )
    {
        if ( isSystem( name ) )
        {
            return this.builtInTypes.getAll().get( name );
        }

        return new RelationshipTypeLoader( this.resourceService ).load( name );
    }


    @Override
    public RelationshipTypes getAll()
    {
        final Set<RelationshipType> list = Sets.newLinkedHashSet();
        list.addAll( this.builtInTypes.getAll().getList() );

        for ( final Application application : this.applicationService.getAllApplications() )
        {
            final RelationshipTypes types = getByApplication( application.getKey() );
            list.addAll( types.getList() );
        }

        return RelationshipTypes.from( list );
    }

    @Override
    public RelationshipTypes getByApplication( final ApplicationKey key )
    {
        if ( SchemaHelper.isSystem( key ) )
        {
            return this.builtInTypes.getByApplication( key );
        }

        final List<RelationshipType> list = Lists.newArrayList();
        for ( final RelationshipTypeName name : findNames( key ) )
        {
            final RelationshipType type = getByName( name );
            if ( type != null )
            {
                list.add( type );
            }

        }

        return RelationshipTypes.from( list );
    }

    private List<RelationshipTypeName> findNames( final ApplicationKey key )
    {
        return new RelationshipTypeLoader( this.resourceService ).findNames( key );
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
