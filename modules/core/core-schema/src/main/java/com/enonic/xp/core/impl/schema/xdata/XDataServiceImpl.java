package com.enonic.xp.core.impl.schema.xdata;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;

@Component(immediate = true)
public final class XDataServiceImpl
    implements XDataService
{
    private final BuiltinXDataTypes builtInTypes;

    private final ApplicationService applicationService;

    private final XDataLoader xDataLoader;

    @Activate
    public XDataServiceImpl( @Reference final ApplicationService applicationService, @Reference final ResourceService resourceService )
    {
        this.builtInTypes = new BuiltinXDataTypes();
        this.applicationService = applicationService;
        this.xDataLoader = new XDataLoader( resourceService );
    }

    @Override
    public XData getByName( final XDataName name )
    {
        if ( SchemaHelper.isSystem( name.getApplicationKey() ) )
        {
            return this.builtInTypes.getAll().getXData( name );
        }

        return xDataLoader.get( name );
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
        final Set<XData> list = new LinkedHashSet<>( this.builtInTypes.getAll().getList() );

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
        if ( SchemaHelper.isSystem( key ) )
        {
            return this.builtInTypes.getByApplication( key );
        }

        final List<XData> list = new ArrayList<>();
        for ( final XDataName name : xDataLoader.findNames( key ) )
        {
            final XData type = getByName( name );
            if ( type != null )
            {
                list.add( type );
            }

        }

        return XDatas.from( list );
    }

    @Override
    public XDatas getFromContentType( final ContentType contentType )
    {
        return XDatas.from(
            contentType.getXData().stream().map( this::getByName ).filter( Objects::nonNull ).collect( Collectors.toSet() ) );
    }
}
