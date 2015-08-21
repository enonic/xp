module api.ui {

    export class Action {

        private label: string;

        private iconClass: string;

        private shortcut: KeyBinding;

        private mnemonic: Mnemonic;

        private enabled: boolean = true;

        private visible: boolean = true;

        protected forceExecute: boolean = false;

        private executionListeners: Function[] = [];

        private propertyChangedListeners: Function[] = [];

        constructor(label: string, shortcut?: string, global?: boolean) {
            this.label = label;

            if (shortcut) {
                this.shortcut = new KeyBinding(shortcut).setGlobal(global).setCallback((e: ExtendedKeyboardEvent, combo: string) => {

                    // preventing Browser shortcuts to kick in
                    if (e.preventDefault) {
                        e.preventDefault();
                    } else {
                        // internet explorer
                        e.returnValue = false;
                    }
                    this.execute();
                    return false;
                });
            }
        }

        getLabel(): string {
            return this.label;
        }

        setLabel(value: string) {

            if (value !== this.label) {
                this.label = value;
                this.notifyPropertyChanged();
            }
        }

        isEnabled(): boolean {
            return this.enabled;
        }

        setEnabled(value: boolean) {

            if (value !== this.enabled) {
                this.enabled = value;
                this.notifyPropertyChanged();
            }
        }

        isVisible(): boolean {
            return this.visible;
        }

        setVisible(value: boolean) {

            if (value !== this.visible) {
                this.visible = value;
                this.notifyPropertyChanged();
            }
        }

        getIconClass(): string {
            return this.iconClass;
        }

        setIconClass(value: string) {

            if (value !== this.iconClass) {
                this.iconClass = value;
                this.notifyPropertyChanged();
            }
        }

        private notifyPropertyChanged() {
            for (var i in this.propertyChangedListeners) {
                this.propertyChangedListeners[i](this);
            }
        }

        hasShortcut(): boolean {
            return this.shortcut != null;
        }

        getShortcut(): KeyBinding {
            return this.shortcut;
        }

        setMnemonic(value: string) {
            this.mnemonic = new Mnemonic(value);
        }

        hasMnemonic(): boolean {
            return this.mnemonic != null;
        }

        getMnemonic(): Mnemonic {
            return this.mnemonic;
        }

        execute(forceExecute: boolean = false): void {
            if (this.enabled) {
                this.forceExecute = forceExecute;
                for (var i in this.executionListeners) {
                    this.executionListeners[i](this);
                }
                this.forceExecute = false;
            }
        }

        onExecuted(listener: (action: Action) => void): Action {
            this.executionListeners.push(listener);
            return this;
        }

        unExecuted(listener: (action: Action) => void): Action {
            this.executionListeners = this.executionListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
        }

        onPropertyChanged(listener: (action: Action) => void) {
            this.propertyChangedListeners.push(listener);
        }

        getKeyBindings(): KeyBinding[] {

            var bindings: KeyBinding[] = [];

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

        static getKeyBindings(actions: api.ui.Action[]): KeyBinding[] {

            var bindings: KeyBinding[] = [];
            actions.forEach((action: Action) => {
                action.getKeyBindings().forEach((keyBinding: KeyBinding) => {
                    bindings.push(keyBinding);
                });

            });
            return bindings;
        }
    }
}
