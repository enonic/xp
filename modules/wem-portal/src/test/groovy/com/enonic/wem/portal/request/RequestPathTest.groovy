package com.enonic.wem.portal.request

import spock.lang.Specification
import spock.lang.Unroll

class RequestPathTest extends Specification
{
    @Unroll
    def "test root path for '#comment'"( )
    {
        def path = RequestPath.from( str );

        expect:
        path.isRoot()
        path.getParent() == null
        path.toString() == '/'

        where:
        str  | comment
        ''   | 'empty'
        '/'  | 'slash'
        '//' | 'double slash'
    }

    @Unroll
    def "test parent path for '#str'"( )
    {
        def path = RequestPath.from( str )

        expect:
        path.getParent() != null
        path.getParent().toString() == parent

        where:
        str      | parent
        '/a'     | '/'
        '/a/b'   | '/a'
        '/a/b/'  | '/a'
        '/a/b/c' | '/a/b'
    }

    @Unroll
    def "test append '#str2' to '#str1'"( )
    {
        def path1 = RequestPath.from( str1 )
        def path2 = path1.append( str2 )
        def path3 = path1.append( RequestPath.from( str2 ) )

        expect:
        path2 != null
        path2.toString() == expected
        path3 != null
        path3.toString() == expected

        where:
        str1  | str2   | expected
        '/'   | 'a'    | '/a'
        'a/b' | 'c'    | '/a/b/c'
        '/a'  | '/b/c' | '/a/b/c'
    }
}
