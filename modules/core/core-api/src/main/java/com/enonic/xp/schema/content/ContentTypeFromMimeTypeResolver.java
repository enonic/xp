package com.enonic.xp.schema.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

public final class ContentTypeFromMimeTypeResolver
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
        MAP.put( "image/bmp", ContentTypeName.imageMedia() );
        MAP.put( "image/x-bmp", ContentTypeName.imageMedia() );
        MAP.put( "image/x-ms-bmp", ContentTypeName.imageMedia() );

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
        MAP.put( "audio/wav", ContentTypeName.audioMedia() );

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
        MAP.put( "video/MP2T", ContentTypeName.videoMedia() );
        MAP.put( "video/avchd-stream", ContentTypeName.videoMedia() );
        MAP.put( "video/mts", ContentTypeName.videoMedia() );
        MAP.put( "video/vnd.dlna.mpeg-tts", ContentTypeName.videoMedia() );
        MAP.put( "application/metastream", ContentTypeName.videoMedia() );
        MAP.put( "application/ogg", ContentTypeName.videoMedia() );

        // Archive
        MAP.put( "application/zip", ContentTypeName.archiveMedia() );
        MAP.put( "application/gzip", ContentTypeName.archiveMedia() );
        MAP.put( "application/x-rar-compressed", ContentTypeName.archiveMedia() );
        MAP.put( "application/x-zip-compressed", ContentTypeName.archiveMedia() );
        MAP.put( "application/x-tar", ContentTypeName.archiveMedia() );

        // Text
        MAP.put( "text/plain", ContentTypeName.textMedia() );
        MAP.put( "text/csv", ContentTypeName.textMedia() );
        MAP.put( "text/rtf", ContentTypeName.textMedia() );

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

        // Document
        MAP.put( "application/pdf", ContentTypeName.documentMedia() );
        MAP.put( "application/postscript", ContentTypeName.documentMedia() );
        MAP.put( "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ContentTypeName.documentMedia() );
        MAP.put( "application/vnd.oasis.opendocument.text", ContentTypeName.documentMedia() );
        MAP.put( "application/msword", ContentTypeName.documentMedia() );

        // Executable
        MAP.put( "application/x-tika-msoffice", ContentTypeName.executableMedia() ); // .msi
        MAP.put( "application/x-msi", ContentTypeName.executableMedia() ); // .msi
        MAP.put( "application/x-msdownload", ContentTypeName.executableMedia() ); // .exe
        MAP.put( "application/exe", ContentTypeName.executableMedia() ); // .exe
        MAP.put( "application/x-exe", ContentTypeName.executableMedia() ); // .exe
        MAP.put( "application/dos-exe", ContentTypeName.executableMedia() ); // .exe
        MAP.put( "vms/exe", ContentTypeName.executableMedia() ); // .exe
        MAP.put( "application/x-winexe", ContentTypeName.executableMedia() ); // .exe
        MAP.put( "application/msdos-windows", ContentTypeName.executableMedia() );  // .exe
        MAP.put( "application/x-apple-diskimage", ContentTypeName.executableMedia() );  // .dmg
        MAP.put( "application/x-sh", ContentTypeName.executableMedia() ); // .sh
        MAP.put( "application/x-shar", ContentTypeName.executableMedia() ); // .sh
        MAP.put( "application/bat", ContentTypeName.executableMedia() ); // .bat
        MAP.put( "application/x-bat", ContentTypeName.executableMedia() ); // .bat

        // Presentation
        MAP.put( "application/vnd.openxmlformats-officedocument.presentationml.presentation", ContentTypeName.presentationMedia() );
        MAP.put( "application/vnd.ms-powerpoint", ContentTypeName.presentationMedia() );
        MAP.put( "application/vnd.oasis.opendocument.presentation", ContentTypeName.presentationMedia() );
        MAP.put( "application/vnd.apple.keynote", ContentTypeName.presentationMedia() );

        // Spreadsheet
        MAP.put( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ContentTypeName.spreadsheetMedia() );
        MAP.put( "application/vnd.ms-excel", ContentTypeName.spreadsheetMedia() );

        // Vector
        MAP.put( "image/svg+xml", ContentTypeName.vectorMedia() );

    }

    public static ContentTypeName resolve( final String mimeType )
    {
        return MAP.get( mimeType );
    }

    public static Set<String> resolveMimeTypes( final ContentTypeNames contentTypeNames )
    {
        return MAP.entrySet().stream().
            filter( entry -> contentTypeNames.contains( entry.getValue() ) ).
            map( HashMap.Entry::getKey ).
            collect( Collectors.toSet() );

    }
}
