package com.enonic.wem.core.content

class ContentSpec extends spock.lang.Specification
{
    def content = new Content();

    // run before every feature method
    def setup( )
    {
        //content = new Content();
    }

    def cleanup( )
    {}       // run after every feature method
    def setupSpec( )
    {}     // run before the first feature method
    def cleanupSpec( )
    {}   // run after the last feature method

    def "setting data on content"( )
    {
        when:
        content.setData( "myText", "myValue" );

        then:
        content.getData( "myText" )
    }
}
