module api.form {

    export class FormValidityChangedEvent extends FormEvent {

        private recording: api.form.ValidationRecording;

        constructor(recording?: api.form.ValidationRecording) {
            super();
            this.recording = recording;
        }

        isValid(): boolean {
            return this.recording.isValid();
        }

        getRecording(): api.form.ValidationRecording {
            return this.recording;
        }
    }
}