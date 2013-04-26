module admin.lib.uri {

    export var baseUrl:String;

    export function getAbsoluteUri(uri:String):String {
        return this.baseUrl + '/' + uri;
    }
}
