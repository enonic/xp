package com.enonic.wem.core.schema.content.serializer;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.MixinReference;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonParsingException;

public class FormItemJsonSerializer
    extends AbstractJsonSerializer<FormItem>
{
    private final InputJsonSerializer inputJsonSerializer;

    private final FormItemSetJsonSerializer formItemSetJsonSerializer;

    private final LayoutJsonSerializer layoutJsonSerializer;

    private final MixinReferenceJsonSerializer mixinReferenceJsonSerializer;

    public FormItemJsonSerializer( final FormItemsJsonSerializer formItemsJsonSerializer )
    {
        this.inputJsonSerializer = new InputJsonSerializer( objectMapper() );
        this.formItemSetJsonSerializer = new FormItemSetJsonSerializer( formItemsJsonSerializer, objectMapper() );
        this.layoutJsonSerializer = new LayoutJsonSerializer( formItemsJsonSerializer, objectMapper() );
        this.mixinReferenceJsonSerializer = new MixinReferenceJsonSerializer( objectMapper() );
    }

    public FormItemJsonSerializer( final FormItemsJsonSerializer formItemsJsonSerializer, final ObjectMapper objectMapper )
    {
        super( objectMapper );
        this.inputJsonSerializer = new InputJsonSerializer( objectMapper );
        this.formItemSetJsonSerializer = new FormItemSetJsonSerializer( formItemsJsonSerializer, objectMapper );
        this.layoutJsonSerializer = new LayoutJsonSerializer( formItemsJsonSerializer, objectMapper );
        this.mixinReferenceJsonSerializer = new MixinReferenceJsonSerializer( objectMapper );
    }

    @Override
    public JsonNode serialize( final FormItem formItem )
    {
        final ObjectNode formItemBaseTypeObject = objectMapper().createObjectNode();
        if ( formItem instanceof FormItemSet )
        {
            formItemBaseTypeObject.put( FormItemSet.class.getSimpleName(), formItemSetJsonSerializer.serialize( (FormItemSet) formItem ) );
        }
        else if ( formItem instanceof Layout )
        {
            formItemBaseTypeObject.put( Layout.class.getSimpleName(), layoutJsonSerializer.serialize( (Layout) formItem ) );
        }
        else if ( formItem instanceof Input )
        {
            formItemBaseTypeObject.put( Input.class.getSimpleName(), inputJsonSerializer.serialize( (Input) formItem ) );
        }
        else if ( formItem instanceof MixinReference )
        {
            formItemBaseTypeObject.put( MixinReference.class.getSimpleName(),
                                        mixinReferenceJsonSerializer.serialize( (MixinReference) formItem ) );
        }
        else
        {
            throw new UnsupportedOperationException( "Unsupported FormItem: " + formItem.getClass().getSimpleName() );
        }
        return formItemBaseTypeObject;
    }

    public FormItem parse( final JsonNode formItemNode )
    {
        final Iterator<String> fieldNamesIt = formItemNode.fieldNames();

        FormItem formItem = null;
        while ( fieldNamesIt.hasNext() )
        {
            final String type = fieldNamesIt.next();

            final JsonNode concreteFormItemObj = formItemNode.get( type );
            if ( type.equals( Input.class.getSimpleName() ) )
            {
                formItem = inputJsonSerializer.parse( concreteFormItemObj );
            }
            else if ( type.equals( FormItemSet.class.getSimpleName() ) )
            {
                formItem = formItemSetJsonSerializer.parse( concreteFormItemObj );
            }
            else if ( type.equals( Layout.class.getSimpleName() ) )
            {
                formItem = layoutJsonSerializer.parse( concreteFormItemObj );
            }
            else if ( type.equals( MixinReference.class.getSimpleName() ) )
            {
                formItem = mixinReferenceJsonSerializer.parse( concreteFormItemObj );
            }
        }

        if ( formItem == null )
        {
            throw new JsonParsingException( "Field describing what type of FormItem was not found: " + formItemNode.toString() );
        }

        return formItem;
    }
}
