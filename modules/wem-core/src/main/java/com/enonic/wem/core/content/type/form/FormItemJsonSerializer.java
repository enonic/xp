package com.enonic.wem.core.content.type.form;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.Layout;
import com.enonic.wem.api.content.type.form.SubTypeReference;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParsingException;

public class FormItemJsonSerializer
    extends AbstractJsonSerializer<FormItem>
{
    private final InputJsonSerializer inputJsonSerializer;

    private final FormItemSetJsonSerializer formItemSetJsonSerializer;

    private final LayoutJsonSerializer layoutJsonSerializer;

    private final SubTypeReferenceJsonSerializer subTypeReferenceJsonSerializer;

    public FormItemJsonSerializer( final FormItemsJsonSerializer formItemsJsonSerializer )
    {
        this.inputJsonSerializer = new InputJsonSerializer();
        this.formItemSetJsonSerializer = new FormItemSetJsonSerializer( formItemsJsonSerializer );
        this.layoutJsonSerializer = new LayoutJsonSerializer( formItemsJsonSerializer );
        this.subTypeReferenceJsonSerializer = new SubTypeReferenceJsonSerializer();
    }

    @Override
    public JsonNode serialize( final FormItem formItem, final ObjectMapper objectMapper )
    {
        final ObjectNode formItemBaseTypeObject = objectMapper.createObjectNode();
        if ( formItem instanceof FormItemSet )
        {
            formItemBaseTypeObject.put( FormItemSet.class.getSimpleName(),
                                        formItemSetJsonSerializer.serialize( (FormItemSet) formItem, objectMapper ) );
        }
        else if ( formItem instanceof Layout )
        {
            formItemBaseTypeObject.put( Layout.class.getSimpleName(), layoutJsonSerializer.serialize( (Layout) formItem, objectMapper ) );
        }
        else if ( formItem instanceof Input )
        {
            formItemBaseTypeObject.put( Input.class.getSimpleName(), inputJsonSerializer.serialize( (Input) formItem, objectMapper ) );
        }
        else if ( formItem instanceof SubTypeReference )
        {
            formItemBaseTypeObject.put( SubTypeReference.class.getSimpleName(),
                                        subTypeReferenceJsonSerializer.serialize( (SubTypeReference) formItem, objectMapper ) );
        }
        else
        {
            throw new UnsupportedOperationException( "Unsupported FormItem: " + formItem.getClass().getSimpleName() );
        }
        return formItemBaseTypeObject;
    }

    public FormItem parse( final JsonNode formItemNode )
    {
        final Iterator<String> fieldNamesIt = formItemNode.getFieldNames();
        if ( !fieldNamesIt.hasNext() )
        {
            throw new JsonParsingException( "Field describing what type of FormItem was not found: " + formItemNode.toString() );
        }

        final String type = fieldNamesIt.next();

        final FormItem formItem;

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
        else if ( type.equals( SubTypeReference.class.getSimpleName() ) )
        {
            formItem = subTypeReferenceJsonSerializer.parse( concreteFormItemObj );
        }
        else
        {
            throw new JsonParsingException( "Unknown FormItem type: " + type );
        }

        return formItem;
    }
}
