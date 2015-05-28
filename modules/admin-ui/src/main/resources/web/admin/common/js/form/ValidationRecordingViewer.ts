module api.form {

    export class ValidationRecordingViewer extends api.ui.Viewer<ValidationRecording> {

        private list: api.dom.UlEl;
        private minText: string = "Min {0} occurrences required";
        private minTextSingle: string = "This field is required";
        private maxText: string = "Max {0} occurrence{1} allowed";

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
            var text = breaksMin ? this.resolveMinText(path) : this.resolveMaxText(path);
            return new api.dom.LiEl().setHtml(text);
        }

        private resolveMinText(path: ValidationRecordingPath): string {
            return path.getMin() > 1 ? api.util.StringHelper.format(this.minText, path.getMin()) : this.minTextSingle;
        }

        private resolveMaxText(path: ValidationRecordingPath): string {
            return api.util.StringHelper.format(this.maxText, path.getMax(), path.getMax() > 1 ? 's' : '');
        }

    }

}