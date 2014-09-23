module api.form {

    export class ValidationRecordingViewer extends api.ui.Viewer<ValidationRecording> {

        private list: api.dom.UlEl;
        private text: string = "{0} occurrence{1} required";

        constructor() {
            super('validation-viewer');
            this.list = new api.dom.UlEl();
            this.appendChild(this.list);
        }

        setObject(recording: ValidationRecording) {
            this.list.removeChildren();
            recording.breaksMinimumOccurrencesArray.forEach((path: ValidationRecordingPath) => {
                this.list.appendChild(this.createItemView(path));
            });
            recording.breaksMaximumOccurrencesArray.forEach((path: ValidationRecordingPath) => {
                this.list.appendChild(this.createItemView(path));
            })
        }

        private createItemView(path: ValidationRecordingPath): api.dom.LiEl {
            var showMax: boolean = path.getMax() > path.getMin();
            var showPlural = showMax || path.getMin() > 1;
            var text = api.util.StringHelper.format(this.text,
                    path.getMin().toString() + (showMax ? ' - ' + path.getMax() : ''),
                showPlural ? 's' : '');
            return new api.dom.LiEl().setHtml(text);
        }

    }

}