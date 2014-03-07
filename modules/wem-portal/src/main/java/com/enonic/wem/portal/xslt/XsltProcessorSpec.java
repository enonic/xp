package com.enonic.wem.portal.xslt;

import java.util.Map;

import javax.xml.transform.Source;

import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Maps.filterValues;

public final class XsltProcessorSpec
{
    private Source xsl;

    private Source source;

    private ImmutableMap<String, Object> parameters;

    public XsltProcessorSpec()
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

    public XsltProcessorSpec xsl( final Source xsl )
    {
        this.xsl = xsl;
        return this;
    }

    public XsltProcessorSpec source( final Source source )
    {
        this.source = source;
        return this;
    }

    public XsltProcessorSpec parameters( final Map<String, Object> params )
    {
        this.parameters = ImmutableMap.copyOf( filterValues( params, notNull() ) );
        return this;
    }
}
