module api.liveedit {

    export class InitializeLiveEditEvent extends api.event.Event {

        private liveEditModel: LiveEditModel;

        constructor(liveEditModel: LiveEditModel) {
            super();
            this.liveEditModel = liveEditModel;
        }

        getLiveEditModel(): LiveEditModel {
            return this.liveEditModel;
        }

        static on(handler: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}