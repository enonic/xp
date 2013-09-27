module app_wizard_form_input {

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
                throw new Error('Input type [' + inputTypeName + '] already registered, unregister it first.');
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

        static createView(inputTypeName:string, inputTypeConfig?:any) {

            if (InputTypeManager.isRegistered(inputTypeName)) {
                var inputType = Object.create(InputTypeManager.inputTypes[inputTypeName].prototype);
                inputType.constructor.call(inputType, inputTypeConfig);
                return inputType;
            }
            else {
                throw new Error("Input type [" + inputTypeName + "] need to be registered first.");
            }
        }
    }
}