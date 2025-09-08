package com.enonic.xp.core.impl.content.schema;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
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
            return this.builtInTypes.getXData( name );
        }

        return xDataLoader.get( name );
    }

    @Override
    public XDatas getByNames( final XDataNames names )
    {
        return names.stream().map( this::getByName ).filter( Objects::nonNull ).collect( XDatas.collector() );
    }

    @Override
    public XDatas getAll()
    {
        final XDatas.Builder builder = XDatas.create();
        builder.addAll( this.builtInTypes.getAll() );

        for ( final Application application : this.applicationService.getInstalledApplications() )
        {
            builder.addAll( getByApplication( application.getKey() ) );
        }

        return builder.build();
    }

    @Override
    public XDatas getByApplication( final ApplicationKey key )
    {
        if ( SchemaHelper.isSystem( key ) )
        {
            return this.builtInTypes.getAll()
                .stream()
                .filter( type -> type.getName().getApplicationKey().equals( key ) )
                .collect( XDatas.collector() );
        }

        return xDataLoader.findNames( key ).stream().map( this::getByName ).filter( Objects::nonNull ).collect(  XDatas.collector() );
    }
}
