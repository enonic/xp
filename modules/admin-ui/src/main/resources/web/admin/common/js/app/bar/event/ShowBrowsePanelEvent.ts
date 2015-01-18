module api.app.bar.event {


    export class ShowBrowsePanelEvent extends api.event.Event {

        static on(handler: (event: ShowBrowsePanelEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowBrowsePanelEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}