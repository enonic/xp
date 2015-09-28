module api.form {

    export class RecordingValidityChangedEvent {

        private origin: api.form.ValidationRecordingPath;

        private recording: api.form.ValidationRecording;

        private inputValueBroken: boolean;

        constructor(recording: api.form.ValidationRecording, origin: api.form.ValidationRecordingPath, inputValueBroken: boolean = false) {
            this.recording = recording;
            this.origin = origin;
            this.inputValueBroken = inputValueBroken;
        }

        getOrigin(): api.form.ValidationRecordingPath {
            return this.origin;
        }

        isValid(): boolean {
            return this.recording.isValid() && !this.inputValueBroken;
        }

        getRecording(): api.form.ValidationRecording {
            return this.recording;
        }

        isInputValueBroken(): boolean {
            return this.inputValueBroken;
        }
    }
}