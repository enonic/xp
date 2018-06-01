package com.enonic.xp.core.impl.schema.xdata;

import java.time.Instant;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.xml.parser.XmlXDataParser;

final class XDataLoader
    extends SchemaLoader<XDataName, XData>
{
    public XDataLoader( final ResourceService resourceService )
    {
        super( resourceService, "/site/x-data" );
    }

    @Override
    protected XData load( final XDataName name, final Resource resource )
    {
        final XData.Builder builder = XData.create();
        parseXml( resource, builder );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    private void parseXml( final Resource resource, final XData.Builder builder )
    {
        final XmlXDataParser parser = new XmlXDataParser();
        parser.currentApplication( resource.getKey().getApplicationKey() );
        parser.source( resource.readString() );
        parser.builder( builder );
        parser.parse();
    }

    @Override
    protected XDataName newName( final ApplicationKey appKey, final String name )
    {
        return XDataName.from( appKey, name );
    }
}
