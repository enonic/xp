module api.app.browse.filter {

    export class BrowseFilterSearchEvent<DATA> extends api.event.Event {
        private data: DATA;

        constructor(data: DATA) {
            super();
            this.data = data;
        }

        getData(): DATA {
            return this.data;
        }

        static on(handler: (event: BrowseFilterSearchEvent<any>) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: BrowseFilterSearchEvent<any>) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
