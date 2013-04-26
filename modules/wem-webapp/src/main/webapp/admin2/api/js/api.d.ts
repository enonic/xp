module admin.api.message {
    function showFeedback(message: String): void;
    function updateAppTabCount(appId, tabCount: Number): void;
    function addListener(name: String, func: Function, scope: any): void;
}
module admin.api.notify {
}
module admin.lib.uri {
    var baseUrl: String;
    function getAbsoluteUri(uri: String): String;
}
