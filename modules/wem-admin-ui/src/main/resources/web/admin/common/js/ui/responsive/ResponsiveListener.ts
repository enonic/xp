module api.ui.responsive {

    export class ResponsiveListener {

        private item: ResponsiveItem;
        private listener: (event?: Event) => void;

        constructor(item: ResponsiveItem, listener: (event?: Event) => void) {
            this.item = item;
            this.listener = listener;
        }

        getItem(): ResponsiveItem {
            return this.item;
        }

        getListener(): (event?: Event) => void {
            return this.listener;
        }
    }
}
