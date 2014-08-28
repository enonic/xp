package com.enonic.wem.xslt;

import java.util.Map;

import javax.xml.transform.Source;

import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Maps.filterValues;

public final class XsltProcessorParams
{
    private Source xsl;

    private Source source;

    private ImmutableMap<String, Object> parameters;

    public XsltProcessorParams()
    {
        this.parameters = ImmutableMap.of();
    }

    public Source getXsl()
    {
        return this.xsl;
    }

    public Source getSource()
    {
        return this.source;
    }

    public Map<String, Object> getParameters()
    {
        return this.parameters;
    }

    public XsltProcessorParams xsl( final Source xsl )
    {
        this.xsl = xsl;
        return this;
    }

    public XsltProcessorParams source( final Source source )
    {
        this.source = source;
        return this;
    }

    public XsltProcessorParams parameters( final Map<String, Object> params )
    {
        this.parameters = ImmutableMap.copyOf( filterValues( params, notNull() ) );
        return this;
    }
}
