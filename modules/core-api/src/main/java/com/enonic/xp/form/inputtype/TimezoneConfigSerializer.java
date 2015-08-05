package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.xml.DomHelper;

abstract class TimezoneConfigSerializer<T extends TimezoneConfig>
    implements InputTypeConfigSerializer<T>
{
    protected final void parseTimezone( final Element elem, T.Builder builder )
    {
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }
    }

    @Override
    public final JsonNode serializeConfig( final T config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        jsonConfig.put( "withTimezone", config.isWithTimezone() );
        return jsonConfig;
    }
}
