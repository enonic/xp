module api.form.inputtype {

    /**
     *      Class to manage input types and their visual representation
     */
    export class InputTypeManager {

        private static inputTypes: { [index: string]: api.Class; } = {};

        static isRegistered(inputTypeClassName: string): boolean {
            return InputTypeManager.inputTypes[inputTypeClassName] != undefined;
        }

        static register(inputTypeClass: api.Class) {

            if (!InputTypeManager.isRegistered(inputTypeClass.getName())) {
                InputTypeManager.inputTypes[inputTypeClass.getName()] = inputTypeClass;
                //console.log('Registered input type [' + inputTypeName + "]");
            }
            else {
                throw new Error('Input type [' + inputTypeClass.getName() + '] is already registered, unregister it first.');
            }
        }

        static unregister(inputTypeName: string) {

            if (InputTypeManager.isRegistered(inputTypeName)) {
                InputTypeManager.inputTypes[inputTypeName] = undefined;
                console.log('Unregistered input type [' + inputTypeName + "]");
            }
            else {
                throw new Error('Input type [' + inputTypeName + '] is not registered.');
            }
        }

        static createView(inputTypeClassName: string, context: InputTypeViewContext): InputTypeView<any> {

            if (InputTypeManager.isRegistered(inputTypeClassName)) {
                var inputTypeClass = InputTypeManager.inputTypes[inputTypeClassName];
                return inputTypeClass.newInstance(context);
            }
            else {
                throw new Error("Input type [" + inputTypeClassName + "] need to be registered first.");
            }
        }
    }
}

/**
 *      Alias to expose InputTypeManager to third parties
 *      To be used for custom javascript development only, use InputTypeManager in typescript instead.
 *
 *      Usage: wem.inputTypes.isRegistered('inputTypeName');
 */
module wem {

    export var inputTypes = api.form.inputtype.InputTypeManager;

}