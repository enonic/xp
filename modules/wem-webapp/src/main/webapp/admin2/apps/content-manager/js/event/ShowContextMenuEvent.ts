module app_event {
    export class ShowContextMenuEvent extends api_event.Event {

        private x:number;
        private y:number;

        constructor(x:number, y:number) {
            this.x = x;
            this.y = y;
            super('showContextMenu');
        }

        getX() {
            return this.x;
        }

        getY() {
            return this.y;
        }

        static on(handler:(event:ShowContextMenuEvent) => void) {
            api_event.onEvent('showContextMenu', handler);
        }
    }
}
