module api.form {

    export class ValidationRecorder {

        private breaksRequiredContract:api.data.DataId[] = [];

        registerBreaksRequiredContract(data:api.data.DataId) {
            this.breaksRequiredContract.push(data)
        }

        // TODO:
    }
}
