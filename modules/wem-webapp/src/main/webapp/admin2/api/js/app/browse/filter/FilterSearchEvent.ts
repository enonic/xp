module api_app_browse_filter {

    export class FilterSearchEvent extends api_event.Event {

        target;

        constructor(target?) {
            super('filterSearch');
            this.target = target;
        }

        getTarget() {
            return this.target;
        }

        static on(handler:(event:FilterSearchEvent) => void) {
            api_event.onEvent('filterSearch', handler);
        }

    }
}
