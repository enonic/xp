module api.form {

    export class RecordingValidityChangedEvent {

        private origin: api.form.ValidationRecordingPath;

        private recording: api.form.ValidationRecording;

        private inputValueBroken: boolean = false;

        private includeChildren: boolean = false;

        constructor(recording: api.form.ValidationRecording, origin: api.form.ValidationRecordingPath) {
            this.recording = recording;
            this.origin = origin;
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

        setInputValueBroken(broken: boolean): RecordingValidityChangedEvent {
            this.inputValueBroken = broken;
            return this;
        }

        isInputValueBroken(): boolean {
            return this.inputValueBroken;
        }

        setIncludeChildren(include: boolean): RecordingValidityChangedEvent {
            this.includeChildren = include;
            return this;
        }

        isIncludeChildren(): boolean {
            return this.includeChildren;
        }
    }
}
