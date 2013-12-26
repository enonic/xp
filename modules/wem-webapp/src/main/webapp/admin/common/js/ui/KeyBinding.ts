module api_ui {

    export class KeyBinding {

        private combination:string;

        private callback:(e:ExtendedKeyboardEvent, combo:string) => boolean;

        private action:string;

        constructor(combination:string, callback?:(e:ExtendedKeyboardEvent, combo:string) => any, action?:string) {

            this.combination = combination;
            this.callback = callback;
            this.action = action;
        }

        setCallback(func:(e:ExtendedKeyboardEvent, combo:string) => boolean):KeyBinding {
            this.callback = func;
            return this;
        }

        setAction(value:string):KeyBinding {
            this.action = value;
            return this;
        }

        getCombination():string {
            return this.combination;
        }

        getCallback():(e:ExtendedKeyboardEvent, combo:string) => boolean {
            return this.callback;
        }

        getAction():string {
            return this.action;
        }

        static newKeyBinding(combination:string):KeyBinding {
            return new KeyBinding(combination);
        }
    }


}