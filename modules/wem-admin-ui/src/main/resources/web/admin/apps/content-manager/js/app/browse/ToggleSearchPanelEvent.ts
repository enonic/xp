module app.browse {

    export class ToggleSearchPanelEvent extends api.event.Event2 {

        static on(handler: (event: ToggleSearchPanelEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ToggleSearchPanelEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}
