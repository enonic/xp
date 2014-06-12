module LiveEdit.ui {

    export class ShaderClickedEvent extends api.event.Event2 {

        static on(handler: (event: ShaderClickedEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ShaderClickedEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}