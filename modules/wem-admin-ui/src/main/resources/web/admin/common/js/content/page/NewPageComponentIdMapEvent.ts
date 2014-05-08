module api.content.page {

    import PageComponentIdMap = api.content.page.PageComponentIdMap;

    export class NewPageComponentIdMapEvent extends api.event.Event2 {

        private map: PageComponentIdMap;

        constructor(map: PageComponentIdMap) {
            super('newPageComponentIdMapEvent.liveEdit');
            this.map = map;
        }

        getMap(): PageComponentIdMap {
            return this.map;
        }

        static on(handler: (event: NewPageComponentIdMapEvent) => void, contextWindow: Window = window) {
            api.event.onEvent2('newPageComponentIdMapEvent.liveEdit', handler, contextWindow);
        }
    }
}