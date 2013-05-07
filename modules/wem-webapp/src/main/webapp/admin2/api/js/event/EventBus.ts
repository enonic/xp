module api.event {

    var bus = new Ext.util.Observable({});

    export function on(name:string, handler:(event:Event) => void) {
        bus.on(name, handler);
    }

    export function fire(event:Event) {
        bus.fireEvent(event.getName(), event);
    }

    /*
     export function fireGlobal(event:Event) {
     bus.fireEvent(event.getName(), event);
     }
     */
}
