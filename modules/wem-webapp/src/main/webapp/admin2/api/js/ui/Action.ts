module api_ui {

    export class Action {

        private label:string;

        private iconClass:string;

        private shortcut:KeyBinding;

        private mnemonic:Mnemonic;

        private enabled:bool = true;

        private executionListeners:Function[] = [];

        private propertyChangeListeners:Function[] = [];

        constructor(label:string, shortcut?:string) {
            this.label = label;

            if (shortcut) {
                this.shortcut = new KeyBinding(shortcut).setCallback(() => {
                    this.execute();
                });
            }
        }

        getLabel():string {
            return this.label;
        }

        setLabel(value:string) {

            if (value !== this.label) {
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

            if (value !== this.iconClass) {
                this.iconClass = value;

                for (var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        }

        hasShortcut():bool {
            return this.shortcut != null;
        }

        getShortcut():KeyBinding {
            return this.shortcut;
        }

        setMnemonic(value:string) {
            this.mnemonic = new Mnemonic(value);
        }

        hasMnemonic():bool {
            return this.mnemonic != null;
        }

        getMnemonic():Mnemonic {
            return this.mnemonic;
        }

        execute():void {

            if (this.enabled) {
                for (var i in this.executionListeners) {
                    this.executionListeners[i](this);
                }
            }
        }

        addExecutionListener(listener:(action:Action) => void):Action {
            this.executionListeners.push(listener);
            return this;
        }

        addPropertyChangeListener(listener:(action:Action) => void) {
            this.propertyChangeListeners.push(listener);
        }

        getKeyBindings():KeyBinding[] {

            var bindings:KeyBinding[] = [];

            if (this.hasShortcut()) {
                bindings.push(this.getShortcut());
            }
            if (this.hasMnemonic()) {
                bindings.push(this.getMnemonic().toKeyBinding(()=> {
                    this.execute();
                }));
            }

            return bindings;
        }

        static getKeyBindings(actions:api_ui.Action[]):KeyBinding[] {

            var bindings:KeyBinding[] = [];
            actions.forEach((action:Action) => {
                action.getKeyBindings().forEach((keyBinding:KeyBinding) => {
                    bindings.push(keyBinding);
                });

            });
            return bindings;
        }
    }
}
