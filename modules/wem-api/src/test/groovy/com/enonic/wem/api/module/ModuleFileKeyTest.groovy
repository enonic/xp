package com.enonic.wem.api.module

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ModuleFileKeyTest
    extends Specification
{
    def "test from uri (#input)"()
    {
        given:
        def key = ModuleFileKey.from( input )

        expect:
        key != null
        key.toString() == uri
        key.getUri() == uri
        key.getPath() == path
        key.getExtension() == ext
        key.getModule().toString() == module
        key.isRoot() == root

        where:
        input                       | uri                       | module           | path       | ext   | root
        "mymodule-1.0.0:"           | "mymodule-1.0.0:/"        | "mymodule-1.0.0" | "/"        | null  | true
        "mymodule-1.0.0:/"          | "mymodule-1.0.0:/"        | "mymodule-1.0.0" | "/"        | null  | true
        "mymodule-1.0.0:a/b.txt"    | "mymodule-1.0.0:/a/b.txt" | "mymodule-1.0.0" | "/a/b.txt" | "txt" | false
        "mymodule-1.0.0:/a/b.txt"   | "mymodule-1.0.0:/a/b.txt" | "mymodule-1.0.0" | "/a/b.txt" | "txt" | false
        "mymodule-1.0.0://a//b.txt" | "mymodule-1.0.0:/a/b.txt" | "mymodule-1.0.0" | "/a/b.txt" | "txt" | false
        "mymodule-1.0.0://a/.."     | "mymodule-1.0.0:/"        | "mymodule-1.0.0" | "/"        | null  | true
        "mymodule-1.0.0://a/./b/.." | "mymodule-1.0.0:/a"       | "mymodule-1.0.0" | "/a"       | null  | false
    }

    def "test from module and path (#input)"()
    {
        given:
        def module = ModuleKey.from( "mymodule-1.0.0" )
        def key = ModuleFileKey.from( module, input )

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
        ModuleFileKey.from( "test" );

        then:
        thrown( IllegalArgumentException )
    }
}
