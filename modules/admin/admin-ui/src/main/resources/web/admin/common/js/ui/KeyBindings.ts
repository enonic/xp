module api.ui {

    import ArrayHelper = api.util.ArrayHelper;
    export class KeyBindings {

        private static instanceCount: number = 0;

        private static INSTANCE: KeyBindings = new KeyBindings();

        private instance: number;

        private activeBindings: Map<string, KeyBinding> = new Map();

        private shelves: Map<string, KeyBinding>[] = [];

        private static debug: boolean = false;

        private helpKeyPressedListeners: {(event: ExtendedKeyboardEvent): void}[] = [];

        public static get(): KeyBindings {
            return KeyBindings.INSTANCE;
        }

        constructor() {
            KeyBindings.instanceCount++;
            this.instance = KeyBindings.instanceCount;
            if (KeyBindings.debug) {
                console.log('KeyBindings constructed instance #' + this.instance);
            }
            this.initializeHelpKey();
        }

        public bindKeys(bindings: KeyBinding[]) {
            let logMessage = 'Binded keys: [';
            bindings.forEach((binding: KeyBinding) => {
                this.bindKey(binding);
                logMessage += `'${binding.getCombination()}' ,`;
            });
            logMessage += ']';
            if (KeyBindings.debug) {
                console.log('KeyBindings[#' + this.instance + '].bindKeys(): ' + logMessage);
            }
        }

        public bindKey(binding: KeyBinding) {
            if (binding.isGlobal()) {
                Mousetrap.bindGlobal(binding.getCombination(), binding.getCallback(),
                    binding.getAction() ? KeyBindingAction[binding.getAction()].toLowerCase() : '');
            } else {
                Mousetrap.bind(binding.getCombination(), binding.getCallback(),
                    binding.getAction() ? KeyBindingAction[binding.getAction()].toLowerCase() : '');
            }
            let bindingKey = this.getBindingKey(binding);
            this.activeBindings.set(bindingKey, binding);
        }

        public unbindKeys(bindings: KeyBinding[]) {

            let logMessage = 'Binded keys: [';

            bindings.forEach((binding: KeyBinding) => {
                this.unbindKey(binding);
                logMessage += `'${binding.getCombination()}' ,`;
            });
            if (KeyBindings.debug) {
                console.log('KeyBindings[#' + this.instance + '].unbindKeys(): ' + logMessage);
            }
        }

        public unbindKey(binding: KeyBinding) {

            Mousetrap.unbind(binding.getCombination());
            delete this.activeBindings.delete(this.getBindingKey(binding));
        }

        public trigger(combination: string, action?: string) {

            Mousetrap.trigger(combination, action);
        }

        public reset() {
            if (KeyBindings.debug) {
                console.log('KeyBindings[#' + this.instance + '].reset()');
            }

            Mousetrap.reset();
            this.activeBindings.clear();
            this.shelves = [];
        }

        public getActiveBindings(): KeyBinding[] {
            return Array.from(this.activeBindings.values());
        }

        /*
         * Stores the current bindings on a new shelf and resets.
         */
        public shelveBindings(keyBindings: KeyBinding[] = []) {
            if (KeyBindings.debug) {
                console.log('KeyBindings[#' + this.instance + '].shelveBindings(): ');
            }
            if (keyBindings.length == 0) {
                Mousetrap.reset();

                this.shelves.push(this.activeBindings);
                this.activeBindings = new Map();
            } else {
                let curBindings: Map<string, KeyBinding> = new Map();

                keyBindings.forEach(binding => {
                    if (this.activeBindings.get(this.getBindingKey(binding))) {
                        curBindings.set(this.getBindingKey(binding), this.activeBindings.get(this.getBindingKey(binding)));
                    }
                });

                if (curBindings.size > 0) {
                    this.unbindKeys(Array.from(curBindings.values()));
                    this.shelves.push(curBindings);
                }
            }
        }

        private getBindingKey(binding: KeyBinding): string {
            return binding.getAction()
                ? binding.getCombination() + '-' + binding.getAction()
                : binding.getCombination();
        }

        /*
         * Resets current bindings and re-binds those from the last shelf.
         */
        public unshelveBindings(keyBindings: KeyBinding[] = []) {

            if (this.shelves.length == 0) {
                if (KeyBindings.debug) {
                    console.log('KeyBindings[#' + this.instance + '].unshelveBindings(): nothing to unshelve');
                }
                return;
            }
            const previousMousetraps: Map<string, KeyBinding> = this.shelves[this.shelves.length - 1];

            if (KeyBindings.debug) {
                console.log('KeyBindings[#' + this.instance + '].unshelveBindings(): unshelving... ');
            }
            if (keyBindings.length == 0) {

                this.activeBindings.clear();
                Mousetrap.reset();

                const previousBindings = Array.from(previousMousetraps.values());

                previousBindings.forEach((previousBinding) => {
                    this.bindKey(previousBinding);
                });

                this.shelves.pop();
            } else {
                const keys = keyBindings.map(binding => this.getBindingKey(binding));

                const previousKeys = Array.from(previousMousetraps.keys());

                previousKeys.forEach((previousKey) => {
                    if (keys.indexOf(previousKey) >= 0) {
                        this.bindKey(previousMousetraps.get(previousKey));
                        previousMousetraps.delete(previousKey);
                    }
                });
                if (previousMousetraps.size == 0) {
                    this.shelves.pop();
                }
            }
        }

        isActive(keyBinding: KeyBinding) {
            const activeBindings: KeyBinding[] = Array.from(this.activeBindings.values());

            return activeBindings.some((curBinding: KeyBinding) => {
                return curBinding == keyBinding ? true : false;
            });
        }

        private initializeHelpKey() {
            this.bindKey(new api.ui.KeyBinding('f2', (e: ExtendedKeyboardEvent) => {
                this.notifyHelpKeyPressed(e);
            }).setGlobal(true).setAction(KeyBindingAction.KEYDOWN));

            this.bindKey(new api.ui.KeyBinding('f2', (e: ExtendedKeyboardEvent) => {
                this.notifyHelpKeyPressed(e);
            }).setGlobal(true).setAction(KeyBindingAction.KEYUP));
        }

        onHelpKeyPressed(listener: (event: ExtendedKeyboardEvent) => void) {
            this.helpKeyPressedListeners.push(listener);
        }

        unHelpKeyPressed(listener: () => void) {
            this.helpKeyPressedListeners =
                this.helpKeyPressedListeners.filter((currentListener: (event: ExtendedKeyboardEvent) => void) => {
                    return listener !== currentListener;
                });
        }

        private notifyHelpKeyPressed(e: ExtendedKeyboardEvent) {
            this.helpKeyPressedListeners.forEach((listener: (event: ExtendedKeyboardEvent) => void) => {
                listener.call(this, e);
            });
        }
    }
}
