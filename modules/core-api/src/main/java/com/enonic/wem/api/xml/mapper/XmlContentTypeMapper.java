package com.enonic.wem.api.xml.mapper;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.model.XmlMetadata;

public final class XmlContentTypeMapper
{
    private final static String SEPARATOR = ":";

    public static XmlContentType toXml( final ContentType object )
    {
        final XmlContentType result = new XmlContentType();
        result.setDisplayName( object.getDisplayName() );
        result.setDescription( object.getDescription() );
        result.setContentDisplayNameScript( object.getContentDisplayNameScript() );
        result.setSuperType( object.getSuperType().toString() );
        result.setIsAbstract( object.isAbstract() );
        result.setIsFinal( object.isFinal() );
        result.setAllowChildContent( object.allowChildContent() );
        for(MixinName mixinName : object.getMetadata().getSet()) {
            XmlMetadata metadata = new XmlMetadata(  );
            metadata.setMixin( Joiner.on( SEPARATOR ).join( mixinName.getModuleKey(), mixinName.getLocalName() ) );
            result.getMetadata().add( metadata );
        }
        result.setForm( XmlFormMapper.toItemsXml( object.form().getFormItems() ) );
        return result;
    }

    public static void fromXml( final ModuleKey currentModule, final XmlContentType xml, final ContentType.Builder builder )
    {
        final XmlModuleRelativeResolver resolver = new XmlModuleRelativeResolver( currentModule );

        builder.displayName( xml.getDisplayName() );
        builder.description( xml.getDescription() );
        builder.contentDisplayNameScript( xml.getContentDisplayNameScript() );
        builder.superType( resolver.toContentTypeName( xml.getSuperType() ) );
        builder.setAbstract( xml.isIsAbstract() != null && xml.isIsAbstract() );
        builder.setFinal( xml.isIsFinal() != null && xml.isIsFinal() );
        builder.allowChildContent( xml.isAllowChildContent() != null && xml.isAllowChildContent() );
        final ImmutableList.Builder<MixinName> metadataMixinNames = ImmutableList.builder();
        for ( XmlMetadata xmlMetadata : xml.getMetadata() )
        {
            final String mixinName = xmlMetadata.getMixin();
            final MixinName metadataSchemaName =
                mixinName.contains( SEPARATOR ) ? MixinName.from( mixinName ) : MixinName.from(currentModule, mixinName );
            metadataMixinNames.add( metadataSchemaName );
        }
        builder.metadata( MixinNames.from( metadataMixinNames.build() ) );
        XmlFormMapper.fromItemsXml( xml.getForm() ).forEach( builder::addFormItem );
    }
}
