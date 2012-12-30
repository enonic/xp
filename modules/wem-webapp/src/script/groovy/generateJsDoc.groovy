import groovy.io.FileType

baseDir = new File( "./" )
appDir = new File( baseDir, "../../main/webapp/admin/resources/app" )

init()

def init()
{
    prependEmptyCommentBlockToAllAppFiles()
    generateDocs()
    removeEmptyCommentBlocks()
}


def prependEmptyCommentBlockToAllAppFiles()
{
    // JSDuck needs at least one block comment at the top of the file in order to generate a document for the class
    def commentString = "/** Dummy */\n"

    appDir.traverse( type: FileType.FILES, nameFilter: ~/.*\.js/ ) {
        def content = it.getText( "utf-8" )
        it.write( commentString + content )
    };
}

def removeEmptyCommentBlocks()
{
    appDir.traverse( type: FileType.FILES, nameFilter: ~/.*\.js/ ) {
        def content = it.getText( "utf-8" )
        def contentWithoutDummyComment = content.replaceAll( /\/\*\* Dummy \*\/\n/, "" )
        it.write( contentWithoutDummyComment )
    };
}

def generateDocs( )
{
    "jsduck $appDir --output docs".execute().text
}
