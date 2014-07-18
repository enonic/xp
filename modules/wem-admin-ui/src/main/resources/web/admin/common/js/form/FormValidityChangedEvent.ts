module api.form {

    export class FormValidityChangedEvent {

        private recording: ValidationRecording;

        constructor(recording?: ValidationRecording) {
            this.recording = recording;
        }

        isValid(): boolean {
            return this.recording.isValid();
        }

        getRecording(): ValidationRecording {
            return this.recording;
        }
    }
}