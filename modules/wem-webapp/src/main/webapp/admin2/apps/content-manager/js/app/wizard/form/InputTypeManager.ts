module app_wizard_form {

    export class InputTypeManager {

        private static inputTypes:{ [index: string]: Function; } = {};

        static isRegistered(inputTypeName:string):boolean {
            return InputTypeManager.inputTypes[inputTypeName] != undefined;
        }

        static register(inputTypeName:string, inputTypeClass:Function) {
            if (!InputTypeManager.isRegistered(inputTypeName)) {
                InputTypeManager.inputTypes[inputTypeName] = inputTypeClass;
                console.log('Registered input type [' + inputTypeName + "]", inputTypeClass);
            } else {
                console.log('Input type [' + inputTypeName + '] already registered, unregister it first.');
            }
        }

        static unregister(inputTypeName:string) {
            if (InputTypeManager.isRegistered(inputTypeName)) {
                InputTypeManager.inputTypes[inputTypeName] = undefined;
                console.log('Unregistered input type [' + inputTypeName + "]");
            } else {
                console.log('Input type [' + inputTypeName + '] is not registered.');
            }
        }

        static createView(inputTypeName:string) {
            if (InputTypeManager.isRegistered(inputTypeName)) {
                var inputType = Object.create(InputTypeManager.inputTypes[inputTypeName].prototype);
                inputType.constructor.apply(inputType);
                console.log("Created input of type [" + inputTypeName + "]", inputType);
                return inputType;
            } else {
                console.log("Input type [" + inputTypeName + "] need to be registered first.");
                return null;
            }
        }

    }

}