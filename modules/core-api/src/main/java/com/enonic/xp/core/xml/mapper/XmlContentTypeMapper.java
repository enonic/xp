package com.enonic.xp.core.xml.mapper;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleRelativeResolver;
import com.enonic.xp.core.schema.content.ContentType;
import com.enonic.xp.core.schema.mixin.MixinName;
import com.enonic.xp.core.schema.mixin.MixinNames;
import com.enonic.xp.core.xml.model.XmlContentType;
import com.enonic.xp.core.xml.model.XmlMetadata;

public final class XmlContentTypeMapper
{
    private final ModuleKey currentModule;

    public XmlContentTypeMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public XmlContentType toXml( final ContentType object )
    {
        final XmlContentType result = new XmlContentType();
        result.setDisplayName( object.getDisplayName() );
        result.setDescription( object.getDescription() );
        result.setContentDisplayNameScript( object.getContentDisplayNameScript() );
        result.setSuperType( object.getSuperType().toString() );
        result.setIsAbstract( object.isAbstract() );
        result.setIsFinal( object.isFinal() );
        result.setAllowChildContent( object.allowChildContent() );
        for ( MixinName mixinName : object.getMetadata().getList() )
        {
            XmlMetadata metadata = new XmlMetadata();
            metadata.setMixin( mixinName.toString() );
            result.getXData().add( metadata );
        }
        result.setForm( new XmlFormMapper( currentModule ).toItemsXml( object.form().getFormItems() ) );
        return result;
    }

    public void fromXml( final XmlContentType xml, final ContentType.Builder builder )
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( currentModule );

        builder.displayName( xml.getDisplayName() );
        builder.description( xml.getDescription() );
        builder.contentDisplayNameScript( xml.getContentDisplayNameScript() );
        builder.superType( resolver.toContentTypeName( xml.getSuperType() ) );
        if ( xml.isIsAbstract() != null )
        {
            builder.setAbstract( xml.isIsAbstract() );
        }
        if ( xml.isIsFinal() != null )
        {
            builder.setFinal( xml.isIsFinal() );
        }
        if ( xml.isAllowChildContent() != null )
        {
            builder.allowChildContent( xml.isAllowChildContent() );
        }

        final ImmutableList.Builder<MixinName> metadataMixinNames = ImmutableList.builder();
        for ( XmlMetadata xmlMetadata : xml.getXData() )
        {
            final String mixinName = xmlMetadata.getMixin();
            final MixinName metadataSchemaName = resolver.toMixinName( mixinName );
            metadataMixinNames.add( metadataSchemaName );
        }
        builder.metadata( MixinNames.from( metadataMixinNames.build() ) );
        new XmlFormMapper( currentModule ).fromItemsXml( xml.getForm() ).forEach( builder::addFormItem );
    }
}
