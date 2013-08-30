module api_ui {

    export class KeyBindings {

        private static mousetraps:any = {};

        private static shelves:Object[] = [];

        static bindKeys(bindings:KeyBinding[]) {

            bindings.forEach((binding:KeyBinding) => {
                KeyBindings.bindKey(binding);
            })
        }

        static bindKey(binding:KeyBinding) {

            console.log("KeyBindings.bindKey", binding);
            Mousetrap.bind(binding.getCombination(), binding.getCallback(), binding.getAction());
            KeyBindings.mousetraps[binding.getCombination()] = binding;
        }

        static unbindKeys(bindings:KeyBinding[]) {

            console.log("KeyBindings.unbindKeys");

            bindings.forEach((binding:KeyBinding) => {
                KeyBindings.unbindKey(binding);
            })
        }

        static unbindKey(binding:KeyBinding) {

            console.log("KeyBindings.unbindKey");

            Mousetrap.unbind(binding.getCombination());
            delete KeyBindings.mousetraps[binding.getCombination()];
        }

        static trigger(combination:string, action?:string) {

            Mousetrap.trigger(combination, action);
        }

        static reset() {

            console.log("KeyBindings.reset");
            Mousetrap.reset();
            KeyBindings.mousetraps = {};
        }

        /*
         * Stores the current bindings on a new shelf and resets.
         */
        static shelveBindings() {

            console.log("shelveBindings() {");
            console.log("  resetting current");
            for (var key in KeyBindings.mousetraps) {
                console.log("  shelving: " + <KeyBinding> KeyBindings.mousetraps[key].getCombination());
            }


            Mousetrap.reset();
            KeyBindings.shelves.push(KeyBindings.mousetraps);
            KeyBindings.mousetraps = {};

            console.log("}");
        }

        /*
         * Resets current bindings and re-binds those from the last shelf.
         */
        static unshelveBindings() {

            console.log("unshelveBindings() {");
            console.log(" resetting current");
            console.log(" removing last shelf");

            Mousetrap.reset();

            var previousMousetraps = KeyBindings.shelves.pop();
            for (var key in previousMousetraps) {
                var mousetrap:KeyBinding = <KeyBinding> previousMousetraps[key];
                console.log("  binding: " + mousetrap.getCombination());
                Mousetrap.bind(mousetrap.getCombination(), mousetrap.getCallback(), mousetrap.getAction());
            }
            KeyBindings.mousetraps = previousMousetraps;

            console.log("}");
        }

    }

    export class KeyBinding {

        private combination:string;

        private callback:(e:ExtendedKeyboardEvent, combo:string) => any;

        private action:string;

        constructor(combination:string, callback?:(e:ExtendedKeyboardEvent, combo:string) => any, action?:string) {

            this.combination = combination;
            this.callback = callback;
            this.action = action;
        }

        setCallback(value:(e:ExtendedKeyboardEvent, combo:string) => any):KeyBinding {
            this.callback = value;
            return this;
        }

        setAction(value:string):KeyBinding {
            this.action = value;
            return this;
        }

        getCombination():string {
            return this.combination;
        }

        getCallback():(e:ExtendedKeyboardEvent, combo:string) => any {
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