module api.form.inputtype.support {

    export class ValidityChangedEvent extends InputTypeEvent {

        private origin: api.form.FormItemPath;

        private recording: api.form.ValidationRecording;

        constructor(recording: api.form.ValidationRecording, origin: api.form.FormItemPath) {
            super();
            this.recording = recording;
            this.origin = origin;
        }

        getOrigin(): api.form.FormItemPath {
            return this.origin;
        }

        isValid(): boolean {
            return this.recording.isValid();
        }

        getRecording(): api.form.ValidationRecording {
            return this.recording;
        }
    }
}