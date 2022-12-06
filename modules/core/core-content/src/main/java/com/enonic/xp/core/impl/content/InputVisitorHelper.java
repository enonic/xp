package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;

public class InputVisitorHelper
{
    public static void visitProperties( final Input input, final PropertyTree data, final Consumer<Property> consumer )
    {
        final List<String> formInputPathElements = input.getPath().getElements();

        final int inputNameElementIndex = formInputPathElements.size() - 1;
        final List<String> formSetPathElements = formInputPathElements.subList( 0, inputNameElementIndex );

        List<PropertySet> currentSets = List.of( data.getRoot() );

        for ( String formElement : formSetPathElements )
        {
            currentSets = currentSets.stream()
                .flatMap( currentSet -> StreamSupport.stream( currentSet.getSets( formElement ).spliterator(), false ) )
                .collect( Collectors.toList() );
        }

        final String inputNameElement = formInputPathElements.get( inputNameElementIndex );

        currentSets.stream().flatMap( currentSet -> currentSet.getProperties( inputNameElement ).stream() ).forEach( consumer );
    }

}
