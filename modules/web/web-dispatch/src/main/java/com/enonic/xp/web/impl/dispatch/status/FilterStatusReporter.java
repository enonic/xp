package com.enonic.xp.web.impl.dispatch.status;


import javax.servlet.Filter;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;

@Component(immediate = true, service = StatusReporter.class)
public final class FilterStatusReporter
    extends ResourceStatusReporter<FilterDefinition>
{
    public FilterStatusReporter()
    {
        super( "http.filter" );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFilter( final Filter filter, final ServiceReference<Filter> filterServiceReference )
    {
        add( ResourceDefinitionFactory.create( filter, getConnectorsFromProperty( filterServiceReference ) ) );
    }

    public void removeFilter( final Filter filter, final ServiceReference<Filter> filterServiceReference )
    {
        remove( ResourceDefinitionFactory.create( filter, getConnectorsFromProperty( filterServiceReference ) ) );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addMapping( final FilterMapping mapping )
    {
        add( ResourceDefinitionFactory.create( mapping ) );
    }

    public void removeMapping( final FilterMapping mapping )
    {
        remove( mapping.getResource() );
    }
}
