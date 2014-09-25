module api.form {

    export class ValidationRecordingViewer extends api.ui.Viewer<ValidationRecording> {

        private list: api.dom.UlEl;
        private minText: string = "{0} occurrence{1} required";
        private maxText: string = "{0} occurrence{1} are allowed";

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

        private createItemView(path: ValidationRecordingPath, breaksMin: boolean): api.dom.LiEl {
            var showPlural = breaksMin && path.getMin() > 1 || !breaksMin && path.getMax() > 1;
            var text = api.util.StringHelper.format(breaksMin ? this.minText : this.maxText,
                breaksMin ? path.getMin() : path.getMax(), showPlural ? 's' : '');
            return new api.dom.LiEl().setHtml(text);
        }

    }

}