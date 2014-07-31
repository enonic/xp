module app.browse {

    export class ShowContextMenuEvent extends api.event.Event2 {

        private x:number;

        private y:number;

        constructor(x:number, y:number) {
            this.x = x;
            this.y = y;
            super();
        }

        getX() {
            return this.x;
        }

        getY() {
            return this.y;
        }

        static on(handler: (event: ShowContextMenuEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowContextMenuEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
