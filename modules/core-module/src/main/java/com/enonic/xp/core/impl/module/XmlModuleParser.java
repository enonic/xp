package com.enonic.xp.core.impl.module;

import java.util.Collections;
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
    private static final String ROOT_TAG_NAME = "site";

    private static final String CONFIG_TAG_NAME = "config";

    private static final String CONTENT_TAG_NAME = "content";

    private static final String META_STEP_TAG_NAME = "meta-step";

    private static final String MIXIN_ATTRIBUTE_NAME = "mixin";

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
        assertTagName( root, ROOT_TAG_NAME );

        final XmlFormMapper formMapper = new XmlFormMapper( this.module.moduleKey );
        this.module.config = formMapper.buildForm( root.getChild( CONFIG_TAG_NAME ) );

        this.module.metaSteps = MixinNames.from( parseMetaSteps( root ) );
    }

    private List<MixinName> parseMetaSteps( final DomElement root )
    {
        final DomElement content = root.getChild( CONTENT_TAG_NAME );

        if ( content == null )
        {
            return Collections.emptyList();
        }
        else
        {
            return content.getChildren( META_STEP_TAG_NAME ).stream().map( this::toMixinName ).collect( Collectors.toList() );
        }

    }

    private MixinName toMixinName( final DomElement metaStep )
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( this.module.getKey() );
        final String name = metaStep.getAttribute( MIXIN_ATTRIBUTE_NAME );
        return resolver.toMixinName( name );
    }
}
