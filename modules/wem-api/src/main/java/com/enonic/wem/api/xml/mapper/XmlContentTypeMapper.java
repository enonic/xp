package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.xml.model.XmlContentType;

public final class XmlContentTypeMapper
{

    public static XmlContentType toXml( final ContentType object )
    {
        final XmlContentType result = new XmlContentType();
        result.setDisplayName( object.getDisplayName() );
        result.setDescription( object.getDescription() );
        result.setContentDisplayNameScript( object.getContentDisplayNameScript() );
        result.setSuperType( object.getSuperType().toString() );
        result.setIsAbstract( object.isAbstract() );
        result.setIsFinal( object.isFinal() );
        result.setIsBuiltIn( object.isBuiltIn() );
        result.setAllowChildContent( object.allowChildContent() );
        result.setForm( XmlFormMapper.toItemsXml( object.form().getFormItems() ) );
        return result;
    }

    public static void fromXml( final XmlContentType xml, final ContentType.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.description( xml.getDescription() );
        builder.contentDisplayNameScript( xml.getContentDisplayNameScript() );
        builder.superType( ContentTypeName.from( xml.getSuperType() ) );
        builder.setAbstract( xml.isIsAbstract() );
        builder.setFinal( xml.isIsFinal() );
        builder.setBuiltIn( xml.isIsBuiltIn() );
        builder.allowChildContent( xml.isAllowChildContent() );
        XmlFormMapper.fromItemsXml( xml.getForm() ).forEach( builder::addFormItem );
    }

}
