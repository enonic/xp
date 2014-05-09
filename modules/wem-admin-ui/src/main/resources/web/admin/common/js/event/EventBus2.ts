module api.event {

    interface HandlersMapEntry {
        customEventHandler: (customEvent: any) => void;
        apiEventHandler: (apiEventObj: api.event.Event2) => void;
    }

    export class EventBus2 {

        private static handlersMap: {[eventName: string]: HandlersMapEntry[]} = {};

        static onEvent(eventName: string, handler: (apiEventObj: api.event.Event2) => void, contextWindow: Window = window) {
            var customEventHandler = (customEvent: any) => handler(customEvent['apiEventObj']);
            if (!this.handlersMap[eventName]) {
                this.handlersMap[eventName] = [];
            }
            this.handlersMap[eventName].push({
                customEventHandler: customEventHandler,
                apiEventHandler: handler
            });
            contextWindow.addEventListener(eventName, customEventHandler);
        }

        static unEvent2(eventName: string, handler: (event: api.event.Event2) => void, contextWindow: Window = window) {
            var customEventHandler: (customEvent: any) => void;
            this.handlersMap[eventName] = this.handlersMap[eventName].filter((entry: HandlersMapEntry) => {
                if (entry.apiEventHandler === handler) {
                    customEventHandler = entry.customEventHandler;
                }
                return entry.apiEventHandler != handler;
            });
            contextWindow.removeEventListener(eventName, customEventHandler);
        }

        static fireEvent(apiEventObj: api.event.Event2, contextWindow: Window = window) {
            var customEvent = contextWindow.document.createEvent('Event');
            customEvent.initEvent(apiEventObj.getName(), true, true);
            customEvent['apiEventObj'] = apiEventObj;
            contextWindow.dispatchEvent(customEvent);
        }

    }
}
