package com.enonic.wem.api.resource

import com.enonic.wem.api.module.ModuleKey
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ResourceKeyTest
    extends Specification
{
    def "test from uri (#input)"()
    {
        given:
        def key = ModuleResourceKey.from( input )

        expect:
        key != null
        key.toString() == uri
        key.getUri() == uri
        key.getPath() == path
        key.getExtension() == ext
        key.getModule().toString() == module
        key.isRoot() == root
        key.getName() == name

        where:
        input                       | uri                       | module           | path       | name    | ext   | root
        "mymodule-1.0.0:"           | "mymodule-1.0.0:/"        | "mymodule-1.0.0" | "/"        | ""      | null  | true
        "mymodule-1.0.0:/"          | "mymodule-1.0.0:/"        | "mymodule-1.0.0" | "/"        | ""      | null  | true
        "mymodule-1.0.0:a/b.txt"    | "mymodule-1.0.0:/a/b.txt" | "mymodule-1.0.0" | "/a/b.txt" | "b.txt" | "txt" | false
        "mymodule-1.0.0:/a/b.txt"   | "mymodule-1.0.0:/a/b.txt" | "mymodule-1.0.0" | "/a/b.txt" | "b.txt" | "txt" | false
        "mymodule-1.0.0://a//b.txt" | "mymodule-1.0.0:/a/b.txt" | "mymodule-1.0.0" | "/a/b.txt" | "b.txt" | "txt" | false
        "mymodule-1.0.0://a/.."     | "mymodule-1.0.0:/"        | "mymodule-1.0.0" | "/"        | ""      | null  | true
        "mymodule-1.0.0://a/./b/.." | "mymodule-1.0.0:/a"       | "mymodule-1.0.0" | "/a"       | "a"     | null  | false
    }

    def "test from module and path (#input)"()
    {
        given:
        def module = ModuleKey.from( "mymodule-1.0.0" )
        def key = ModuleResourceKey.from( module, input )

        expect:
        key != null
        key.toString() == uri
        key.getUri() == uri
        key.getPath() == path
        key.getExtension() == ext
        key.getModule() == module
        key.isRoot() == root

        where:
        input        | uri                       | path       | ext   | root
        ""           | "mymodule-1.0.0:/"        | "/"        | null  | true
        "/"          | "mymodule-1.0.0:/"        | "/"        | null  | true
        "a/b.txt"    | "mymodule-1.0.0:/a/b.txt" | "/a/b.txt" | "txt" | false
        "/a/b.txt"   | "mymodule-1.0.0:/a/b.txt" | "/a/b.txt" | "txt" | false
        "//a//b.txt" | "mymodule-1.0.0:/a/b.txt" | "/a/b.txt" | "txt" | false
        "//a/.."     | "mymodule-1.0.0:/"        | "/"        | null  | true
        "//a/./b/.." | "mymodule-1.0.0:/a"       | "/a"       | null  | false
    }

    def "test invalid uri"()
    {
        when:
        ModuleResourceKey.from( "test" );

        then:
        thrown( IllegalArgumentException )
    }

    def "test (#key1) #op (#key2)"()
    {
        expect:
        ModuleResourceKey.from( key1 ).equals( ModuleResourceKey.from( key2 ) ) == flag

        where:
        key1                  | key2                  | flag  | op
        "mymodule-1.0.0:/"    | "mymodule-1.0.0:/"    | true  | "equals"
        "mymodule-1.0.0:"     | "mymodule-1.0.0:/"    | true  | "equals"
        "mymodule-1.0.0:/a/b" | "mymodule-1.0.0:/a/b" | true  | "equals"
        "mymodule-1.0.0:/a"   | "mymodule-1.0.0:/a/b" | false | "not equals"
        "mymodule-1.0.0:/a/b" | "mymodule-1.1.0:/a/b" | false | "not equals"
    }

    def "test hash code"()
    {
        given:
        def key1 = ModuleResourceKey.from( "mymodule-1.0.0:/a/b" )
        def key2 = ModuleResourceKey.from( "mymodule-1.0.0:/a/b" )
        def key3 = ModuleResourceKey.from( "mymodule-1.0.0:/a" )

        expect:
        key1.hashCode() == key2.hashCode()
        key1.hashCode() != key3.hashCode()
    }

    def "test resolve (#path) from (#uri)"()
    {
        given:
        def key1 = ModuleResourceKey.from( uri )
        def key2 = key1.resolve( path )

        expect:
        key2 != null
        key2.toString() == resolved

        where:
        uri                   | path   | resolved
        "mymodule-1.0.0:/"    | ""     | "mymodule-1.0.0:/"
        "mymodule-1.0.0:/"    | "."    | "mymodule-1.0.0:/"
        "mymodule-1.0.0:/"    | "/"    | "mymodule-1.0.0:/"
        "mymodule-1.0.0:/a/b" | "../c" | "mymodule-1.0.0:/a/c"
        "mymodule-1.0.0:/a"   | "b/c"  | "mymodule-1.0.0:/a/b/c"
    }
}
