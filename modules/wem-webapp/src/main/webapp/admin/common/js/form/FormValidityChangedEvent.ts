module api.form {

    export class FormValidityChangedEvent extends FormEvent {

        private recording: api.form.ValidationRecorder;

        constructor(recording?: api.form.ValidationRecorder) {
            super();
            this.recording = recording;
        }

        isValid(): boolean {
            return this.recording.isValid();
        }

        getRecorder(): api.form.ValidationRecorder {
            return this.recording;
        }
    }
}