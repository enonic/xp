package com.enonic.xp.core.impl.site;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlFormMapper;
import com.enonic.xp.xml.parser.XmlObjectParser;

final class XmlSiteParser
    extends XmlObjectParser<XmlSiteParser>
{
    private static final String ROOT_TAG_NAME = "site";

    private static final String CONFIG_TAG_NAME = "config";

    private static final String META_STEP_TAG_NAME = "x-data";

    private static final String MIXIN_ATTRIBUTE_NAME = "mixin";

    private ApplicationKey applicationKey;

    private SiteDescriptor.Builder siteDescriptorBuilder;

    public XmlSiteParser applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }

    public XmlSiteParser siteDescriptorBuilder( final SiteDescriptor.Builder siteDescriptorBuilder )
    {
        this.siteDescriptorBuilder = siteDescriptorBuilder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, ROOT_TAG_NAME );

        final XmlFormMapper formMapper = new XmlFormMapper( applicationKey );
        this.siteDescriptorBuilder.form( formMapper.buildForm( root.getChild( CONFIG_TAG_NAME ) ) );
        this.siteDescriptorBuilder.metaSteps( MixinNames.from( parseMetaSteps( root ) ) );
    }

    private List<MixinName> parseMetaSteps( final DomElement root )
    {
        return root.getChildren( META_STEP_TAG_NAME ).stream().map( this::toMixinName ).collect( Collectors.toList() );
    }

    private MixinName toMixinName( final DomElement metaStep )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( applicationKey );
        final String name = metaStep.getAttribute( MIXIN_ATTRIBUTE_NAME );
        return resolver.toMixinName( name );
    }
}
