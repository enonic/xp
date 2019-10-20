package com.enonic.xp;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import com.google.common.reflect.ClassPath;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class EqualsHashcodeTest
{
    @TestFactory
    Stream<DynamicTest> verifyEqualsHashcode()
        throws Exception
    {
        final Predicate<Class<?>> equals = clazz -> {
            try
            {
                if ( clazz.isInterface() || Modifier.isAbstract( clazz.getModifiers() ) )
                {
                    return false;
                }
                clazz.getDeclaredMethod( "equals", Object.class );
                return true;
            }
            catch ( NoSuchMethodException e )
            {
                return false;
            }
        };

        return ClassPath.from( ClassLoader.getSystemClassLoader() ).
            getTopLevelClassesRecursive( getClass().getPackageName() ).
            stream().
            map( ClassPath.ClassInfo::load ).
            filter( equals ).
            map( clazz -> {
                final Executable test = () -> EqualsVerifier.forClass( clazz ).suppress( Warning.NULL_FIELDS ).verify();
                return DynamicTest.dynamicTest( clazz.getName(), test );
            } );
    }
}
