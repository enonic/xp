module api.form.inputtype {

    /**
     *      Class to manage input types and their visual representation
     */
    export class InputTypeManager {

        private static inputTypes: { [name: string]: api.Class; } = {};

        static isRegistered(inputTypeName: string): boolean {
            var name = InputTypeManager.normalize(inputTypeName);
            return InputTypeManager.inputTypes[name] != undefined;
        }

        static register(inputTypeClass: api.Class) {
            var name = InputTypeManager.normalize(inputTypeClass.getName());

            if (!InputTypeManager.isRegistered(name)) {
                InputTypeManager.inputTypes[name] = inputTypeClass;
            }
            else {
                throw new Error('Input type [' + name + '] is already registered, unregister it first.');
            }
        }

        static unregister(inputTypeName: string) {
            var name = InputTypeManager.normalize(inputTypeName);

            if (InputTypeManager.isRegistered(name)) {
                InputTypeManager.inputTypes[name] = undefined;
                console.log('Unregistered input type [' + name + "]");
            }
            else {
                throw new Error('Input type [' + name + '] is not registered.');
            }
        }

        static createView(inputTypeName: string, context: InputTypeViewContext): InputTypeView<any> {
            var name = InputTypeManager.normalize(inputTypeName);

            if (InputTypeManager.isRegistered(name)) {
                var inputTypeClass = InputTypeManager.inputTypes[name];
                return inputTypeClass.newInstance(context);
            }
            else {
                throw new Error("Input type [" + name + "] need to be registered first.");
            }
        }

        private static normalize(inputTypeName: string): string {
            return (inputTypeName || '').toLowerCase();
        }
    }
}
