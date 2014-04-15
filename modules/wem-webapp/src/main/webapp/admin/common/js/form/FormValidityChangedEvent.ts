module api.form {

    export class FormValidityChangedEvent extends FormEvent {

        private recording: ValidationRecording;

        constructor(recording?: ValidationRecording) {
            super();
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