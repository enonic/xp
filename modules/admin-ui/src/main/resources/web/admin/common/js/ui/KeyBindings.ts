module api.ui {

    export class KeyBindings {

        private static instanceCount: number = 0;

        private static INSTANCE: KeyBindings = new KeyBindings();

        private instance: number;

        private activeBindings: {[s:string] : KeyBinding;} = {};

        private shelves: {[s:string] : KeyBinding;}[] = [];

        private static debug: boolean = false;

        public static get(): KeyBindings {
            return KeyBindings.INSTANCE;
        }

        constructor() {
            KeyBindings.instanceCount++;
            this.instance = KeyBindings.instanceCount;
            if (KeyBindings.debug) {
                console.log("KeyBindings constructed instance #" + this.instance);
            }
        }

        public bindKeys(bindings: KeyBinding[]) {

            var logMessage = "Binded keys: [";
            bindings.forEach((binding: KeyBinding) => {
                this.bindKey(binding);
                logMessage += "'" + binding.getCombination() + "' ,";
            });
            logMessage += "]";
            if (KeyBindings.debug) {
                console.log("KeyBindings[#" + this.instance + "].bindKeys(): " + logMessage);
            }
        }

        public bindKey(binding: KeyBinding) {
            if (binding.isGlobal()) {
                Mousetrap.bindGlobal(binding.getCombination(), binding.getCallback(), binding.getAction());
            } else {
                Mousetrap.bind(binding.getCombination(), binding.getCallback(), binding.getAction());
            }
            this.activeBindings[binding.getCombination()] = binding;
        }

        public unbindKeys(bindings: KeyBinding[]) {

            var logMessage = "Binded keys: [";

            bindings.forEach((binding: KeyBinding) => {
                this.unbindKey(binding);
                logMessage += "'" + binding.getCombination() + "' ,";
            });
            if (KeyBindings.debug) {
                console.log("KeyBindings[#" + this.instance + "].unbindKeys(): " + logMessage);
            }
        }

        public unbindKey(binding: KeyBinding) {

            Mousetrap.unbind(binding.getCombination());
            delete this.activeBindings[binding.getCombination()];
        }

        public trigger(combination: string, action?: string) {

            Mousetrap.trigger(combination, action);
        }

        public reset() {
            if (KeyBindings.debug) {
                console.log("KeyBindings[#" + this.instance + "].reset()");
            }

            Mousetrap.reset();
            this.activeBindings = {};
            this.shelves = [];
        }

        /*
         * Stores the current bindings on a new shelf and resets.
         */
        public shelveBindings() {
            if (KeyBindings.debug) {
                console.log("KeyBindings[#" + this.instance + "].shelveBindings(): ");
            }
            Mousetrap.reset();
            this.shelves.push(this.activeBindings);
            this.activeBindings = {};
        }

        /*
         * Resets current bindings and re-binds those from the last shelf.
         */
        public unshelveBindings() {

            var previousMousetraps: {[s:string] : KeyBinding;} = this.shelves.pop();
            if (previousMousetraps == undefined) {
                if (KeyBindings.debug) {
                    console.log("KeyBindings[#" + this.instance + "].unshelveBindings(): nothing to unshelve");
                }
                return;
            }
            if (KeyBindings.debug) {
                console.log("KeyBindings[#" + this.instance + "].unshelveBindings(): unshelving... ");
            }

            this.activeBindings ={};
            Mousetrap.reset();

            for (var key in previousMousetraps) {
                var mousetrap: KeyBinding = <KeyBinding> previousMousetraps[key];
                this.bindKey(mousetrap);
            }
        }
    }
}