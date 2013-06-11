module api_action {

    export class Action {

        private label:string;

        private iconClass:string;

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

            if( value !== this.label ) {
                this.label = value;

                for (var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        }

        isEnabled():bool {
            return this.enabled;
        }

        setEnabled(value:bool) {

            if (value !== this.enabled) {
                this.enabled = value;

                for (var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        }

        getIconClass():string {
            return this.iconClass;
        }

        setIconClass(value:string) {

            if( value !== this.iconClass ) {
                this.iconClass = value;

                for (var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        }

        execute():void {

            if (this.enabled) {
                for (var i in this.executionListeners) {
                    this.executionListeners[i](this);
                }
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
