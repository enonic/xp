module API.event {

    export class Event {
        private name:string;

        constructor(name:string) {
            this.name = name;
        }

        getName():string {
            return this.name;
        }

        fire() {
            fireEvent(this);
        }
    }

}
