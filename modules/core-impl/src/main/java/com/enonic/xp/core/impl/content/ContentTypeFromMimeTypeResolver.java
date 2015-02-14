package com.enonic.xp.core.impl.content;


import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.schema.content.ContentTypeName;

final class ContentTypeFromMimeTypeResolver
{
    private final static Map<String, ContentTypeName> MAP = Maps.newHashMap();

    static
    {
        // Image
        MAP.put( "image/gif", ContentTypeName.imageMedia() );
        MAP.put( "image/jpeg", ContentTypeName.imageMedia() );
        MAP.put( "image/pjpeg", ContentTypeName.imageMedia() );
        MAP.put( "image/png", ContentTypeName.imageMedia() );
        MAP.put( "image/tiff", ContentTypeName.imageMedia() );
        MAP.put( "image/vnd.djvu", ContentTypeName.imageMedia() );

        // Audio
        MAP.put( "audio/basic", ContentTypeName.audioMedia() );
        MAP.put( "audio/L24", ContentTypeName.audioMedia() );
        MAP.put( "audio/mp3", ContentTypeName.audioMedia() );
        MAP.put( "audio/mp4", ContentTypeName.audioMedia() );
        MAP.put( "audio/mpeg", ContentTypeName.audioMedia() );
        MAP.put( "audio/ogg", ContentTypeName.audioMedia() );
        MAP.put( "audio/opus", ContentTypeName.audioMedia() );
        MAP.put( "audio/vorbis", ContentTypeName.audioMedia() );
        MAP.put( "audio/vnd.rn-realaudio", ContentTypeName.audioMedia() );
        MAP.put( "audio/vnd.wave", ContentTypeName.audioMedia() );
        MAP.put( "audio/webm", ContentTypeName.audioMedia() );

        // Video
        MAP.put( "video/avi", ContentTypeName.videoMedia() );
        MAP.put( "video/mpeg", ContentTypeName.videoMedia() );
        MAP.put( "video/mp4", ContentTypeName.videoMedia() );
        MAP.put( "video/ogg", ContentTypeName.videoMedia() );
        MAP.put( "video/quicktime", ContentTypeName.videoMedia() );
        MAP.put( "video/webm", ContentTypeName.videoMedia() );
        MAP.put( "video/x-matroska", ContentTypeName.videoMedia() );
        MAP.put( "video/x-ms-wmv", ContentTypeName.videoMedia() );
        MAP.put( "video/x-flv", ContentTypeName.videoMedia() );
        MAP.put( "application/ogg", ContentTypeName.videoMedia() );

        // Archive
        MAP.put( "application/zip", ContentTypeName.archiveMedia() );
        MAP.put( "application/gzip", ContentTypeName.archiveMedia() );

        // Text
        MAP.put( "text/plain", ContentTypeName.textMedia() );
        MAP.put( "text/csv", ContentTypeName.textMedia() );

        // Code
        MAP.put( "application/xml", ContentTypeName.codeMedia() );
        MAP.put( "application/xml-dtd", ContentTypeName.codeMedia() );
        MAP.put( "application/json", ContentTypeName.codeMedia() );
        MAP.put( "application/xhtml+xml", ContentTypeName.codeMedia() );
        MAP.put( "application/javascript", ContentTypeName.codeMedia() );
        MAP.put( "application/ecmascript", ContentTypeName.codeMedia() );
        MAP.put( "text/xml", ContentTypeName.codeMedia() );
        MAP.put( "text/html", ContentTypeName.codeMedia() );
        MAP.put( "text/css", ContentTypeName.codeMedia() );
        MAP.put( "text/javascript", ContentTypeName.codeMedia() );
        MAP.put( "application/soap+xml", ContentTypeName.codeMedia() );

        // Data
        MAP.put( "text/rtf", ContentTypeName.textMedia() );

        // Document
        MAP.put( "application/pdf", ContentTypeName.documentMedia() );
        MAP.put( "application/postscript", ContentTypeName.documentMedia() );
        MAP.put( "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ContentTypeName.documentMedia() );

        // Executable
        // TODO

        // Presentation
        MAP.put( "application/vnd.openxmlformats-officedocument.presentationml.presentation", ContentTypeName.presentationMedia() );

        // Vector
        MAP.put( "image/svg+xml", ContentTypeName.vectorMedia() );

    }

    static ContentTypeName resolve( final String mimeType )
    {
        return MAP.get( mimeType );
    }
}
