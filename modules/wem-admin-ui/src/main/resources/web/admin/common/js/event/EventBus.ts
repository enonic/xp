module api.event {

    interface HandlersMapEntry {
        customEventHandler: (customEvent: any) => void;
        apiEventHandler: (apiEventObj: api.event.Event2) => void;
    }

    var bus = new Ext.util.Observable({});

    var handlersMap: {[eventName: string]: HandlersMapEntry[]} = {};

    export function onEvent(name:string, handler:(event:api.event.Event) => void) {
        bus.on(name, handler);
    }

    export function fireEvent(event:api.event.Event) {
        bus.fireEvent(event.getName(), event);
    }

    export function onEvent2(eventName: string, handler:(apiEventObj:api.event.Event2) => void, contextWindow: Window = window) {
        var customEventHandler = (customEvent: any) => handler(customEvent['apiEventObj']);
        if (!handlersMap[eventName]) {
            handlersMap[eventName] = [];
        }
        handlersMap[eventName].push({
            customEventHandler: customEventHandler,
            apiEventHandler: handler
        });
        contextWindow.addEventListener(eventName, customEventHandler);
    }

    export function unEvent2(eventName: string, handler:(event: api.event.Event2) => void, contextWindow: Window = window) {
        var customEventHandler: (customEvent: any) => void;
        handlersMap[eventName] = handlersMap[eventName].filter((entry: HandlersMapEntry) => {
            if (entry.apiEventHandler === handler) {
                customEventHandler = entry.customEventHandler;
            }
            return entry.apiEventHandler != handler;
        });
        contextWindow.removeEventListener(eventName, customEventHandler);
    }

    export function fireEvent2(apiEventObj: api.event.Event2, contextWindow: Window = window) {
        var customEvent = contextWindow.document.createEvent('Event');
        customEvent.initEvent(apiEventObj.getName(), true, true);
        customEvent['apiEventObj'] = apiEventObj;
        contextWindow.dispatchEvent(customEvent);
    }
}
