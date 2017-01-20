module api.ui.treegrid {

    export class TreeGridItemClickedEvent extends api.event.Event {

        private repeatedSelection: boolean;

        constructor(repeatedSelection?: boolean) {
            super();
            this.repeatedSelection = repeatedSelection;
        }

        isRepeatedSelection(): boolean {
            return this.repeatedSelection;
        }

        static on(handler: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
