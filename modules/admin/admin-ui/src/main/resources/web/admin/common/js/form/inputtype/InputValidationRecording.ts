module api.form.inputtype {

    export class InputValidationRecording {

        private breaksMinimumOccurrences: boolean;

        private breaksMaximumOccurrences: boolean;

        private additionalValidationRecord: AdditionalValidationRecord;

        constructor() {
            this.breaksMinimumOccurrences = false;
            this.breaksMaximumOccurrences = false;
        }

        isValid(): boolean {
            return !this.breaksMaximumOccurrences && !this.breaksMinimumOccurrences;
        }

        setBreaksMinimumOccurrences(value: boolean) {
            this.breaksMinimumOccurrences = value;
        }

        setBreaksMaximumOccurrences(value: boolean) {
            this.breaksMaximumOccurrences = value;
        }

        setAdditionalValidationRecord(value: AdditionalValidationRecord) {
            this.additionalValidationRecord = value;
        }

        isMinimumOccurrencesBreached(): boolean {
            return this.breaksMinimumOccurrences;
        }

        isMaximumOccurrencesBreached(): boolean {
            return this.breaksMaximumOccurrences;
        }

        getAdditionalValidationRecord(): AdditionalValidationRecord {
            return this.additionalValidationRecord;
        }

        equals(that: InputValidationRecording): boolean {

            if (this.breaksMinimumOccurrences != that.breaksMinimumOccurrences) {
                return false;
            }

            if (this.breaksMaximumOccurrences != that.breaksMaximumOccurrences) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.additionalValidationRecord, that.additionalValidationRecord)) {
                return false;
            }

            return true;
        }

        validityChanged(other: InputValidationRecording) {
            return other == undefined || other == null || !other.equals(this);
        }
    }
}