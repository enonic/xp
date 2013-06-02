module API_event {

    var bus = new Ext.util.Observable({});

    export function onEvent(name:string, handler:(event:Event) => void) {
        bus.on(name, handler);
    }

    export function fireEvent(event:Event) {
        bus.fireEvent(event.getName(), event);
    }

    /*
     export function fireGlobal(event:Event) {
     bus.fireEvent(event.getName(), event);
     }
     */
}
