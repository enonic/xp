module api.form {

    export class ValidityChangedEvent {

        private origin: api.form.ValidationRecordingPath;

        private recording: api.form.ValidationRecording;

        constructor(recording: api.form.ValidationRecording, origin: api.form.ValidationRecordingPath) {
            this.recording = recording;
            this.origin = origin;
        }

        getOrigin(): api.form.ValidationRecordingPath {
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