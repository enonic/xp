package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.xml.model.XmlMixin;

public final class XmlMixinMapper
{
    private final ModuleKey currentModule;

    public XmlMixinMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public XmlMixin toXml( final Mixin object )
    {
        final XmlMixin result = new XmlMixin();
        result.setDisplayName( object.getDisplayName() );
        result.setDescription( object.getDescription() );
        result.setItems( new XmlFormMapper( currentModule ).toItemsXml( object.getFormItems() ) );
        return result;
    }

    public void fromXml( final XmlMixin xml, final Mixin.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.description( xml.getDescription() );

        final FormItems items = new FormItems();
        new XmlFormMapper( currentModule ).fromItemsXml( xml.getItems() ).forEach( items::add );

        builder.formItems( items );
    }
}
