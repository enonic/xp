module API.notify {
    function showFeedback(message: String): void;
    function updateAppTabCount(appId, tabCount: Number): void;
    function addListener(name: String, func: Function, scope: any): void;
}
module API.notify {
}
module API.util {
    var baseUri: String;
    function getAbsoluteUri(uri: String): String;
}
module API.event {
    class Event {
        private name;
        constructor(name: string);
        public getName(): string;
        public fire(): void;
    }
}
module API.event {
    function onEvent(name: string, handler: (event: Event) => void): void;
    function fireEvent(event: Event): void;
}
