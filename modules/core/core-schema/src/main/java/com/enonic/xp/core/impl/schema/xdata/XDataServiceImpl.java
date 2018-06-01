package com.enonic.xp.core.impl.schema.xdata;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;

@Component(immediate = true)
public final class XDataServiceImpl
    implements XDataService
{
    private ApplicationService applicationService;

    private ResourceService resourceService;

    public XDataServiceImpl()
    {
    }

    @Override
    public XData getByName( final XDataName name )
    {
        final XData xData = new XDataLoader( this.resourceService ).get( name );
        if ( xData != null )
        {
            return xData;
        }
        return null;
    }

    @Override
    public XDatas getByNames( final XDataNames names )
    {
        if ( names == null )
        {
            return XDatas.empty();
        }

        return XDatas.from( names.stream().map( this::getByName ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
    }

    @Override
    public XDatas getAll()
    {
        final Set<XData> list = Sets.newLinkedHashSet();

        for ( final Application application : this.applicationService.getInstalledApplications() )
        {
            final XDatas types = getByApplication( application.getKey() );
            list.addAll( types.getList() );
        }

        return XDatas.from( list );
    }

    @Override
    public XDatas getByApplication( final ApplicationKey key )
    {
        final List<XData> list = Lists.newArrayList();
        for ( final XDataName name : findNames( key ) )
        {
            final XData type = getByName( name );
            if ( type != null )
            {
                list.add( type );
            }

        }

        return XDatas.from( list );
    }

    private Set<XDataName> findNames( final ApplicationKey key )
    {
        return new XDataLoader( this.resourceService ).findNames( key );
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
}
