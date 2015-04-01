package com.enonic.xp.core.impl.module;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlFormMapper;
import com.enonic.xp.xml.parser.XmlObjectParser;

final class XmlModuleParser
    extends XmlObjectParser<XmlModuleParser>
{
    private ModuleImpl module;

    public XmlModuleParser module( final ModuleImpl module )
    {
        this.module = module;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "module" );

        final XmlFormMapper formMapper = new XmlFormMapper( this.module.moduleKey );
        this.module.config = formMapper.buildForm( root.getChild( "config" ) );

        this.module.metaSteps = MixinNames.from( parseMetaSteps( root ) );
    }

    private List<MixinName> parseMetaSteps( final DomElement root )
    {
        return root.getChildren( "x-data" ).stream().map( this::toMixinName ).collect( Collectors.toList() );
    }

    private MixinName toMixinName( final DomElement root )
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( this.module.getKey() );
        final String name = root.getAttribute( "mixin" );
        return resolver.toMixinName( name );
    }
}
