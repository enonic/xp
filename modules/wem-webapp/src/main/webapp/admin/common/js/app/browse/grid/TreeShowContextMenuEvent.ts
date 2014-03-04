module api.app.browse.grid {

    export class TreeShowContextMenuEvent {

        private x: number;

        private y: number;

        constructor(x: number, y: number) {
            this.x = x;
            this.y = y;
        }

        getX(): number {
            return this.x;
        }

        getY(): number {
            return this.y;
        }
    }
}