module admin.lib.uri {

    export function getAbsoluteUri(uri:String):String {
        return CONFIG.baseUrl + '/' + uri;
    }
}