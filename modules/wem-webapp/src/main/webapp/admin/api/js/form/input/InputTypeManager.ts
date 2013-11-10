module api_form_input {

    /**
     *      Class to manage input types and their visual representation
     */
    export class InputTypeManager {

        private static inputTypes:{ [index: string]: Function; } = {};

        static isRegistered(inputTypeName:string):boolean {
            return InputTypeManager.inputTypes[inputTypeName] != undefined;
        }

        static register(inputTypeName:string, inputTypeClass:Function) {

            if (!InputTypeManager.isRegistered(inputTypeName)) {
                InputTypeManager.inputTypes[inputTypeName] = inputTypeClass;
                console.log('Registered input type [' + inputTypeName + "]");
            }
            else {
                throw new Error('Input type [' + inputTypeName + '] is already registered, unregister it first.');
            }
        }

        static unregister(inputTypeName:string) {

            if (InputTypeManager.isRegistered(inputTypeName)) {
                InputTypeManager.inputTypes[inputTypeName] = undefined;
                console.log('Unregistered input type [' + inputTypeName + "]");
            }
            else {
                throw new Error('Input type [' + inputTypeName + '] is not registered.');
            }
        }

        static createView(inputTypeName:string, config?:api_form_input_type.InputTypeViewConfig<any>):api_form_input_type.InputTypeView {

            if (InputTypeManager.isRegistered(inputTypeName)) {
                var inputType = Object.create(InputTypeManager.inputTypes[inputTypeName].prototype);
                inputType.constructor.call(inputType, config);
                return inputType;
            }
            else {
                throw new Error("Input type [" + inputTypeName + "] need to be registered first.");
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

    export var inputTypes = api_form_input.InputTypeManager;

}