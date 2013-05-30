module API_action {

    export class Action {

        private label:string;

        private enabled:bool = true;

        private executionListeners:Function[] = [];

        private propertyChangeListeners:Function[] = [];

        constructor(label:string) {
            this.label = label;
        }

        getLabel():string {
            return this.label;
        }

        setLabel(value:string) {
            this.label = value;
        }

        isEnabled():bool {
            return this.enabled;
        }

        setEnabled(value:bool) {

            this.enabled = value;

            for (var i in this.propertyChangeListeners) {
                this.propertyChangeListeners[i](this);
            }
        }

        execute():void {

            for (var i in this.executionListeners) {
                this.executionListeners[i](this);
            }
        }

        addExecutionListener(listener:(action:Action) => void) {
            this.executionListeners.push(listener);
        }

        addPropertyChangeListener(listener:(action:Action) => void) {
            this.propertyChangeListeners.push(listener);
        }
    }
}
