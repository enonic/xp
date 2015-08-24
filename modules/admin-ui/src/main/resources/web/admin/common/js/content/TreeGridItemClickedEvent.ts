module api.content {

    export class TreeGridItemClickedEvent extends api.event.Event {

        static on(handler: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}