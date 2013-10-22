module api_form {

    export class ValidationRecorder {

        private breaksRequiredContract:api_data.DataId[] = [];

        registerBreaksRequiredContract(data:api_data.DataId) {
            this.breaksRequiredContract.push(data)
        }

        // TODO:
    }
}
