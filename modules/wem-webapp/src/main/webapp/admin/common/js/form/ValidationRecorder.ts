module api.form {

    // TODO: Rename ValidationRecording
    export class ValidationRecorder {

        private breaksMinimumOccurrencesArray: FormItemPath[] = [];

        private breaksMaximumOccurrencesArray: FormItemPath[] = [];

        private breaksRequiredContract: api.data.DataId[] = [];

        breaksMinimumOccurrences(formItem: FormItemPath) {
            this.breaksMinimumOccurrencesArray.push(formItem);
        }

        breaksMaximumOccurrences(formItem: FormItemPath) {
            this.breaksMaximumOccurrencesArray.push(formItem);
        }

        registerBreaksRequiredContract(data: api.data.DataId) {
            this.breaksRequiredContract.push(data)
        }

        isValid(): boolean {
            return this.breaksMinimumOccurrencesArray.length == 0 && this.breaksMaximumOccurrencesArray.length == 0;
        }

        flatten(recorder: ValidationRecorder) {
            this.breaksMinimumOccurrencesArray = this.breaksMinimumOccurrencesArray.concat(recorder.breaksMinimumOccurrencesArray);
            this.breaksMaximumOccurrencesArray = this.breaksMaximumOccurrencesArray.concat(recorder.breaksMaximumOccurrencesArray);
        }

        removeByPath(path: api.form.FormItemPath) {

            console.log("ValidationRecorder.removeByPath(" + path.toString() + ")");
            console.log(" before remove: ");
            this.print();
            var pathAsString = path.toString();

            for (var i = this.breaksMinimumOccurrencesArray.length - 1; i >= 0; i--) {
                if (this.breaksMinimumOccurrencesArray[i].toString().indexOf(pathAsString) == 0) {
                    this.breaksMinimumOccurrencesArray.splice(i, 1);
                }
            }

            for (var i = this.breaksMaximumOccurrencesArray.length - 1; i >= 0; i--) {
                if (this.breaksMaximumOccurrencesArray[i].toString().indexOf(pathAsString) == 0) {
                    this.breaksMaximumOccurrencesArray.splice(i, 1);
                }
            }
            console.log(" after remove: ");
            this.print();
        }

        equals(other: ValidationRecorder): boolean {

            if (this.breaksMinimumOccurrencesArray.length != other.breaksMinimumOccurrencesArray.length) {
                return false;
            }
            else if (this.breaksMaximumOccurrencesArray.length != other.breaksMaximumOccurrencesArray.length) {
                return false;
            }

            for (var i = 0; i < this.breaksMinimumOccurrencesArray.length; i++) {
                if (this.breaksMinimumOccurrencesArray[i].toString() != other.breaksMinimumOccurrencesArray[i].toString()) {
                    return false;
                }
            }

            for (var i = 0; i < this.breaksMaximumOccurrencesArray.length; i++) {
                if (this.breaksMaximumOccurrencesArray[i].toString() != other.breaksMaximumOccurrencesArray[i].toString()) {
                    return false;
                }
            }

            return true;
        }

        validityChanged(previous: api.form.ValidationRecorder): boolean {
            return previous == undefined || previous == null || !previous.equals(this);
        }

        print() {
            this.breaksMinimumOccurrencesArray.forEach((path: FormItemPath, index: number) => {
                console.log("  breaksMinimumOccurrencesArray[" + index + "] = " + path.toString());
            });

            this.breaksMaximumOccurrencesArray.forEach((path: FormItemPath, index: number) => {
                console.log("  breaksMaximumOccurrencesArray[" + index + "] = " + path.toString());
            });

        }
    }
}
