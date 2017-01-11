module api.ui.treegrid {

    export class TreeGridItemClickedEvent extends api.event.Event {

        private selection: boolean;

        constructor(selection: boolean = false) {
            super();
            this.selection = selection;
        }

        public hasSelection() {
            return this.selection;
        }
        
        static on(handler: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}