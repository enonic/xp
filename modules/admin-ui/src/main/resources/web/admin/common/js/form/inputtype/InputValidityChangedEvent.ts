module api.form.inputtype {

    export class InputValidityChangedEvent {

        private inputName: string;

        private recording: InputValidationRecording;

        constructor(recording: InputValidationRecording, inputName:string) {
            this.recording = recording;
            this.inputName = inputName;
        }

        getInputName(): string {
            return this.inputName;
        }

        isValid(): boolean {
            return this.recording.isValid();
        }

        getRecording(): InputValidationRecording {
            return this.recording;
        }
    }
}