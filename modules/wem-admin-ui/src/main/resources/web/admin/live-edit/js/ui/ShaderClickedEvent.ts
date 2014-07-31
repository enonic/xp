module LiveEdit.ui {

    export class ShaderClickedEvent extends api.event.Event {

        static on(handler: (event: ShaderClickedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ShaderClickedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}