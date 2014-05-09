module api.content.page {

    import Event2 = api.event.Event2;
    import PageComponentIdMap = api.content.page.PageComponentIdMap;

    export class NewPageComponentIdMapEvent extends Event2 {

        private map: PageComponentIdMap;

        constructor(map: PageComponentIdMap) {
            super('newPageComponentIdMapEvent.liveEdit');
            this.map = map;
        }

        getMap(): PageComponentIdMap {
            return this.map;
        }

        static on(handler: (event: NewPageComponentIdMapEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind('newPageComponentIdMapEvent.liveEdit', handler, contextWindow);
        }

        static un(handler: (event: NewPageComponentIdMapEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind('newPageComponentIdMapEvent.liveEdit', handler, contextWindow);
        }
    }
}