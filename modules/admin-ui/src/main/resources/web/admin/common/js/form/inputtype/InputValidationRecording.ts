module api.form.inputtype {

    export class InputValidationRecording {

        private breaksMinimumOccurrences: boolean;

        private breaksMaximumOccurrences: boolean;

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

        isMinimumOccurrenesBreached(): boolean {
            return this.breaksMinimumOccurrences;
        }

        isMaximumOccurrenesBreached(): boolean {
            return this.breaksMaximumOccurrences;
        }

        equals(that: InputValidationRecording): boolean {

            if (this.breaksMinimumOccurrences != that.breaksMinimumOccurrences) {
                return false;
            }

            if (this.breaksMaximumOccurrences != that.breaksMaximumOccurrences) {
                return false;
            }

            return true;
        }

        validityChanged(other: InputValidationRecording) {
            return other == undefined || other == null || !other.equals(this);
        }
    }
}