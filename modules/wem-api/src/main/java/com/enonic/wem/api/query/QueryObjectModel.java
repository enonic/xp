package com.enonic.wem.api.query;

import java.util.Collection;

import com.enonic.wem.api.facet.Facets;

public interface QueryObjectModel
{
    public Constraint getConstraint();

    public Collection<Ordering> getOrderings();

    public Facets getFacets();

}
