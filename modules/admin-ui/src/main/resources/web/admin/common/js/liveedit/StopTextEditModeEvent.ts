module api.liveedit {

    export class StopTextEditModeEvent extends api.event.Event {

        constructor() {
            super();
        }

        static on(handler: (event: StopTextEditModeEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: StopTextEditModeEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}