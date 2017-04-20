package com.enonic.xp.core.impl.content.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.util.Reference;

final class ReferenceVisitor
    extends InputVisitor
{
    private static final String REFERENCE_PREFIX = "_";

    private static final String REFERENCE_SUFFIX = "_references";

    private final PropertyTree propertyTree;

    private final Parser<ContentIds> parser;

    public ReferenceVisitor( final PropertyTree propertyTree, final Parser<ContentIds> parser )
    {
        this.propertyTree = propertyTree;
        this.parser = parser;
    }

    @Override
    public void visit( final Input input )
    {

        if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
        {
            final Value value = propertyTree.getValue( input.getPath().toString() );
            if ( value != null && !value.isNull() )
            {

                final String parentPath = input.getPath().getParent().toString();

                final PropertySet parentSet =
                    StringUtils.isEmpty( parentPath ) ? propertyTree.getRoot() : propertyTree.getSet( parentPath );

                parser.parse( value.asString() ).stream().
                    forEach( id ->
                             {
                                 final Reference ref = com.enonic.xp.util.Reference.from( id.toString() );
                                 parentSet.addReference( REFERENCE_PREFIX + input.getName() + REFERENCE_SUFFIX, ref );
                             } );
            }
        }
    }

    public void removeOldReferences()
    {
        doRemoveReferences( this.propertyTree.getRoot() );
    }

    private void doRemoveReferences( final PropertySet data )
    {

        List<String> names = Lists.newArrayList();

        data.getProperties().forEach( property ->
                                      {
                                          if ( ValueTypes.REFERENCE.equals( property.getType() ) )
                                          {
                                              final String name = property.getName();
                                              if ( name.startsWith( REFERENCE_PREFIX ) && name.endsWith( REFERENCE_SUFFIX ) )
                                              {
                                                  names.add( name );
                                              }
                                          }
                                          else if ( ValueTypes.PROPERTY_SET.equals( property.getType() ) )
                                          {
                                              doRemoveReferences( property.getSet() );
                                          }
                                      } );

        names.forEach( name -> data.removeProperties( name ) );
    }


}
