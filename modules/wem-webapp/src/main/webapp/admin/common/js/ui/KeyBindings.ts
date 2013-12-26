module api_ui {

    export class KeyBindings {

        private static instanceCount: number = 0;

        private static INSTANCE: KeyBindings = new KeyBindings();

        private instance: number;

        private activeBindings: {[s:string] : KeyBinding;} = {};

        private shelves: {[s:string] : KeyBinding;}[] = [];

        public static get(): KeyBindings {
            return KeyBindings.INSTANCE;
        }

        constructor() {
            KeyBindings.instanceCount++;
            this.instance = KeyBindings.instanceCount;
            console.log("KeyBindings constructed instance #" + this.instance);
        }

        public bindKeys(bindings: KeyBinding[]) {

            var logMessage = "Binded keys: [";
            bindings.forEach((binding: KeyBinding) => {
                this.bindKey(binding);
                logMessage += "'" + binding.getCombination() + "' ,";
            });
            logMessage += "]";

            console.log("KeyBindings[#" + this.instance + "].bindKeys(): " + logMessage);
        }

        private bindKey(binding: KeyBinding) {

            Mousetrap.bind(binding.getCombination(), binding.getCallback(), binding.getAction());
            this.activeBindings[binding.getCombination()] = binding;
        }

        public unbindKeys(bindings: KeyBinding[]) {

            var logMessage = "Binded keys: [";

            bindings.forEach((binding: KeyBinding) => {
                this.unbindKey(binding);
                logMessage += "'" + binding.getCombination() + "' ,";
            })

            console.log("KeyBindings[#" + this.instance + "].unbindKeys(): " + logMessage);
        }

        private unbindKey(binding: KeyBinding) {

            Mousetrap.unbind(binding.getCombination());
            delete this.activeBindings[binding.getCombination()];
        }

        public trigger(combination: string, action?: string) {

            Mousetrap.trigger(combination, action);
        }

        public reset() {
            console.log("KeyBindings[#" + this.instance + "].reset()");

            Mousetrap.reset();
            this.activeBindings = {};
            this.shelves = [];
        }

        /*
         * Stores the current bindings on a new shelf and resets.
         */
        public shelveBindings() {
            console.log("KeyBindings[#" + this.instance + "].shelveBindings(): ");
            Mousetrap.reset();
            this.shelves.push(this.activeBindings);
            this.activeBindings = {};
        }

        /*
         * Resets current bindings and re-binds those from the last shelf.
         */
        public unshelveBindings() {

            Mousetrap.reset();
            var previousMousetraps: {[s:string] : KeyBinding;} = this.shelves.pop();
            if (previousMousetraps == undefined) {
                console.log("KeyBindings[#" + this.instance + "].unshelveBindings(): nothing to unshelve");
                return;
            }

            console.log("KeyBindings[#" + this.instance + "].unshelveBindings(): unshelving... ");
            for (var key in previousMousetraps) {
                var mousetrap: KeyBinding = <KeyBinding> previousMousetraps[key];
                Mousetrap.bind(mousetrap.getCombination(), mousetrap.getCallback(), mousetrap.getAction());
            }
            this.activeBindings = previousMousetraps;
        }
    }
}