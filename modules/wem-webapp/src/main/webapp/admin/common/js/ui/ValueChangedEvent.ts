module api.ui {

    export class ValueChangedEvent extends InputEvent {

        private oldValue:string;

        private newValue:string;

        constructor(oldValue:string, newValue:string) {
            super();
            this.oldValue = oldValue;
            this.newValue = newValue;
        }


        getOldValue():string {
            return this.oldValue;
        }

        getNewValue():string {
            return this.newValue;
        }
    }
}