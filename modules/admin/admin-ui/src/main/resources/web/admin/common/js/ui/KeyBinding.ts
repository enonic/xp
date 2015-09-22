module api.ui {

    export enum KeyBindingAction {
        KEYDOWN,
        KEYUP,
        KEYPRESS
    }

    export class KeyBinding {

        private combination: string;

        private callback: (e: ExtendedKeyboardEvent, combo: string) => boolean;

        private action: KeyBindingAction;

        private global: boolean;

        constructor(combination: string, callback?: (e: ExtendedKeyboardEvent, combo: string) => any, action?: KeyBindingAction,
                    global?: boolean) {

            this.combination = combination;
            this.callback = callback;
            this.action = action;
            this.global = global;
        }

        setCallback(func: (e: ExtendedKeyboardEvent, combo: string) => boolean): KeyBinding {
            this.callback = func;
            return this;
        }

        setAction(value: KeyBindingAction): KeyBinding {
            this.action = value;
            return this;
        }

        setGlobal(global: boolean): KeyBinding {
            this.global = global;
            return this;
        }

        getCombination(): string {
            return this.combination;
        }

        getCallback(): (e: ExtendedKeyboardEvent, combo: string) => boolean {
            return this.callback;
        }

        getAction(): KeyBindingAction {
            return this.action;
        }

        isGlobal(): boolean {
            return this.global;
        }

        static newKeyBinding(combination: string): KeyBinding {
            return new KeyBinding(combination);
        }

        static createMultiple(callback: (e: ExtendedKeyboardEvent, combo: string) => any, ...combinations:string[]) {
            var bindings:KeyBinding[] = [];

            combinations.forEach((combination:string) => {
                bindings.push(new KeyBinding(combination, callback))
            });

            return bindings;
        }
    }


}