package com.enonic.xp.lib.content.deserializer;

import java.util.List;
import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

public final class SiteConfigDeserializer
{
    private final PropertyTreeTranslator translator;

    public SiteConfigDeserializer( PropertyTreeTranslator translator )
    {
        this.translator = translator;
    }

    public void deserialize( Object siteConfig, ContentTypeName typeName, PropertySet into )
    {
        if ( siteConfig instanceof Map )
        {
            deserializeSingle( (Map<String, Object>) siteConfig, typeName, into );
        }
        else if ( siteConfig instanceof List )
        {
            for ( Map<String, Object> cfg : (List<Map<String, Object>>) siteConfig )
            {
                deserializeSingle( cfg, typeName, into );
            }
        }
    }

    private void deserializeSingle( Map<String, Object> config, ContentTypeName typeName, PropertySet into )
    {
        ApplicationKey key = ApplicationKey.from( config.get( "applicationKey" ).toString() );
        Map<String, Object> values = (Map<String, Object>) config.get( "config" );
        if ( values == null )
        {
            return;
        }

        PropertySet set = into.addSet( ContentPropertyNames.SITECONFIG );
        set.addString( "applicationKey", key.toString() );
        PropertyTree translated = translator.translate( values, key, typeName );
        set.addSet( "config", translated.getRoot().copy( set.getTree() ) );
    }
}

