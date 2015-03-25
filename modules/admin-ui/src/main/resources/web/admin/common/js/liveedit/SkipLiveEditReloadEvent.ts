module api.liveedit {

    export class SkipLiveEditReloadEvent extends api.event.Event {

        private skip: boolean;

        constructor(skip: boolean) {
            super();
            this.skip = skip;
        }

        isSkip(): boolean {
            return this.skip;
        }

        static on(handler: (event: SkipLiveEditReloadEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: SkipLiveEditReloadEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}