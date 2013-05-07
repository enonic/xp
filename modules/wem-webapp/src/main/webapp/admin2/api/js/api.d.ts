module admin.api.message {
    function showFeedback(message: String): void;
    function updateAppTabCount(appId, tabCount: Number): void;
    function addListener(name: String, func: Function, scope: any): void;
}
module admin.api.notify {
}
module admin.lib.uri {
    var baseUri: String;
    function getAbsoluteUri(uri: String): String;
}
module api.event {
    class Event {
        private name;
        constructor(name: string);
        public getName(): string;
    }
}
module api.event {
    function on(name: string, handler: (event: Event) => void): void;
    function fire(event: Event): void;
}
