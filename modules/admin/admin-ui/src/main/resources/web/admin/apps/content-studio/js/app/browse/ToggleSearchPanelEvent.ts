module app.browse {

    export class ToggleSearchPanelEvent extends api.event.Event {

        static on(handler: (event: ToggleSearchPanelEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ToggleSearchPanelEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }

}
