module api.liveedit {

    import Event2 = api.event.Event2;

    export class NewPageComponentIdMapEvent extends Event2 {

        private map: PageComponentIdMap;

        constructor(map: PageComponentIdMap) {
            super();
            this.map = map;
        }

        getMap(): PageComponentIdMap {
            return this.map;
        }

        static on(handler: (event: NewPageComponentIdMapEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: NewPageComponentIdMapEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}