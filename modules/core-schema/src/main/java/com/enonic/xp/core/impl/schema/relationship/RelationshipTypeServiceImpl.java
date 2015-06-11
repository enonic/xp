package com.enonic.xp.core.impl.schema.relationship;

import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Component;
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
    implements RelationshipTypeService, SynchronousBundleListener
{
    private final Map<ModuleKey, RelationshipTypes> relationshipTypesMap;

    private ModuleService moduleService;

    public RelationshipTypeServiceImpl()
    {
        this.relationshipTypesMap = Maps.newConcurrentMap();
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
        for ( Module module : moduleService.getAllModules() )
        {
            final RelationshipTypes relationshipTypes = getByModule( module.getKey() );
            relationshipTypeList.addAll( relationshipTypes.getList() );
        }

        return RelationshipTypes.from( relationshipTypeList );
    }

    @Override
    public RelationshipTypes getByModule( final ModuleKey moduleKey )
    {
        return relationshipTypesMap.computeIfAbsent( moduleKey, moduleKeyParam -> {
            RelationshipTypes relationshipTypes = null;
            RelationshipTypeProvider relationshipTypeProvider = null;

            //If the module is the default module
            if ( ModuleKey.SYSTEM.equals( moduleKeyParam ) )
            {
                //takes as provider the default RelationshipTypes provider
                relationshipTypeProvider = new BuiltinRelationshipTypesProvider();
            }
            else
            {
                //Else, creates a provider with the corresponding bundle
                final Module module = moduleService.getModule( moduleKeyParam );
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
        } );
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        relationshipTypesMap.remove( ModuleKey.from( event.getBundle() ) );
    }
}
