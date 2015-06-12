package com.enonic.xp.core.impl.schema.relationship;

import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeProvider;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;

@Component(immediate = true)
public final class RelationshipTypeServiceImpl
    implements RelationshipTypeService, BundleListener
{
    private final Map<ModuleKey, RelationshipTypes> relationshipTypesMap;

    private ModuleService moduleService;

    private BundleContext context;

    public RelationshipTypeServiceImpl()
    {
        this.relationshipTypesMap = Maps.newConcurrentMap();
    }

    @Activate
    public void start( final ComponentContext context )
    {
        this.context = context.getBundleContext();
        this.context.addBundleListener( this );
    }

    @Deactivate
    public void stop()
    {
        this.context.removeBundleListener( this );
    }

    @Override
    public RelationshipType getByName( final RelationshipTypeName name )
    {
        final RelationshipTypes relationshipTypes = getByModule( name.getModuleKey() );
        return relationshipTypes.get( name );
    }

    @Override
    public RelationshipTypes getAll()
    {
        final Set<RelationshipType> relationshipTypeList = Sets.newLinkedHashSet();

        //Gets the default RelationshipTypes
        final RelationshipTypes systemRelationshipTypes = getByModule( ModuleKey.SYSTEM );
        relationshipTypeList.addAll( systemRelationshipTypes.getList() );

        //Gets for each module the RelationshipTypes
        for ( Module module : this.moduleService.getAllModules() )
        {
            final RelationshipTypes relationshipTypes = getByModule( module.getKey() );
            relationshipTypeList.addAll( relationshipTypes.getList() );
        }

        return RelationshipTypes.from( relationshipTypeList );
    }

    @Override
    public RelationshipTypes getByModule( final ModuleKey moduleKey )
    {
        return relationshipTypesMap.computeIfAbsent( moduleKey, this::loadByModule );
    }

    private RelationshipTypes loadByModule( final ModuleKey moduleKey )
    {
        RelationshipTypes relationshipTypes = null;
        RelationshipTypeProvider relationshipTypeProvider = null;

        //If the module is the default module
        if ( ModuleKey.SYSTEM.equals( moduleKey ) )
        {
            //takes as provider the default RelationshipTypes provider
            relationshipTypeProvider = new BuiltinRelationshipTypesProvider();
        }
        else
        {
            //Else, creates a provider with the corresponding bundle
            final Module module = this.moduleService.getModule( moduleKey );
            if ( module != null )
            {
                relationshipTypeProvider = BundleRelationshipTypeProvider.create( module.getBundle() );
            }
        }

        if ( relationshipTypeProvider != null )
        {
            relationshipTypes = relationshipTypeProvider.get();
        }

        if ( relationshipTypes == null )
        {
            relationshipTypes = RelationshipTypes.empty();
        }

        return relationshipTypes;
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( BundleEvent.UPDATED == event.getType() || BundleEvent.UNINSTALLED == event.getType() )
        {
            this.relationshipTypesMap.remove( ModuleKey.from( event.getBundle() ) );
        }
    }
}
