module api_event {

    export class FilterSearchEvent extends Event {

        target;

        constructor(target?) {
            super('filterSearch');
            this.target = target;
        }

        getTarget() {
            return this.target;
        }

    }
}
