module api.form {

    export class FormValidityChangedEvent {

        private recording: ValidationRecording;

        private atLeastOneInputValueBroken: boolean;

        constructor(recording?: ValidationRecording, atLeastOneInputValueBroken: boolean = false) {
            this.recording = recording;
            this.atLeastOneInputValueBroken = atLeastOneInputValueBroken;
        }

        isValid(): boolean {
            return this.recording.isValid() && !this.atLeastOneInputValueBroken;
        }

        getRecording(): ValidationRecording {
            return this.recording;
        }
    }
}