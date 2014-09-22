module api.form {

    export class ValidationRecordingViewer extends api.ui.Viewer<ValidationRecording> {

        private list: api.dom.UlEl;

        constructor() {
            super('validation-viewer');
            this.list = new api.dom.UlEl();
            this.appendChild(this.list);
        }

        setObject(recording: ValidationRecording) {
            this.list.removeChildren();
            recording.breaksMinimumOccurrencesArray.forEach((path: ValidationRecordingPath) => {
                this.list.appendChild(this.createItemView(path, true));
            });
            recording.breaksMaximumOccurrencesArray.forEach((path: ValidationRecordingPath) => {
                this.list.appendChild(this.createItemView(path, false));
            })
        }

        private createItemView(path: ValidationRecordingPath, breaksMin?: boolean): api.dom.LiEl {
            return new api.dom.LiEl().setHtml(path.getDataName() +
                                              (breaksMin ? " - minimum " + path.getMin() : " - maximum " + path.getMax()) +
                                              " occurrence(s) must exist");
        }

    }

}