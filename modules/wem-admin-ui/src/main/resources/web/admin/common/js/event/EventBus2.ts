module api.event {

    interface HandlersMapEntry {
        customEventHandler: (customEvent: any) => void;
        apiEventHandler: (apiEventObj: api.event.Event2) => void;
    }

    export class EventBus2 {

        private static handlersMap: {[eventName: string]: HandlersMapEntry[]} = {};

        static onEvent(eventName: string, handler: (apiEventObj: api.event.Event2) => void, contextWindow: Window = window) {
            var customEventHandler = (customEvent: any) => handler(customEvent['apiEventObj']);
            if (!EventBus2.handlersMap[eventName]) {
                EventBus2.handlersMap[eventName] = [];
            }
            EventBus2.handlersMap[eventName].push({
                customEventHandler: customEventHandler,
                apiEventHandler: handler
            });
            contextWindow.addEventListener(eventName, customEventHandler);
        }

        static unEvent2(eventName: string, handler?: (event: api.event.Event2) => void, contextWindow: Window = window) {
            if (handler) {
                var customEventHandler: (customEvent: any) => void;
                EventBus2.handlersMap[eventName] = EventBus2.handlersMap[eventName].filter((entry: HandlersMapEntry) => {
                    if (entry.apiEventHandler === handler) {
                        customEventHandler = entry.customEventHandler;
                    }
                    return entry.apiEventHandler != handler;
                });
                contextWindow.removeEventListener(eventName, customEventHandler);
            } else {
                (EventBus2.handlersMap[eventName] || []).forEach((entry: HandlersMapEntry) => {
                    contextWindow.removeEventListener(eventName, entry.customEventHandler);
                });
                EventBus2.handlersMap[eventName] = [];
            }
        }

        static fireEvent(apiEventObj: api.event.Event2, contextWindow: Window = window) {
            var customEvent = contextWindow.document.createEvent('Event');
            customEvent.initEvent(apiEventObj.getName(), true, true);
            customEvent['apiEventObj'] = apiEventObj;
            contextWindow.dispatchEvent(customEvent);
        }

    }
}
