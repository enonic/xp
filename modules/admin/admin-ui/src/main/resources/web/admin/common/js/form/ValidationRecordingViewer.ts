module api.form {
    import i18n = api.util.i18n;

    export class ValidationRecordingViewer extends api.ui.Viewer<ValidationRecording> {

        private list: api.dom.UlEl;

        constructor() {
            super('validation-viewer');
            this.list = new api.dom.UlEl();
        }

        doLayout(object: ValidationRecording) {
            super.doLayout(object);

            if (!this.list.isRendered()) {
                this.appendChild(this.list);
            } else {
                this.list.removeChildren();
            }

            if (object && this.list.getChildren().length === 0) {
                object.getBreakMinimumOccurrences().forEach((path: ValidationRecordingPath) => {
                    this.list.appendChild(this.createItemView(path, true));
                });
                object.getBreakMaximumOccurrences().forEach((path: ValidationRecordingPath) => {
                    this.list.appendChild(this.createItemView(path, false));
                });
            }
        }

        appendValidationMessage(message: string, removeExisting: boolean = true) {
            if (removeExisting) {
                this.list.removeChildren();
            }
            this.list.appendChild(new api.dom.LiEl().setHtml(message));
        }

        setError(text: string) {
            this.list.removeChildren();
            if (text) {
                this.list.appendChild(new api.dom.LiEl().setHtml(text));
            }
        }

        private createItemView(path: ValidationRecordingPath, breaksMin: boolean): api.dom.LiEl {
            let text = breaksMin ? this.resolveMinText(path) : this.resolveMaxText(path);
            return new api.dom.LiEl().setHtml(text);
        }

        private resolveMinText(path: ValidationRecordingPath): string {
            return path.getMin() > 1 ? i18n('field.occurrence.breaks.min', path.getMin()) : i18n('field.value.required');
        }

        private resolveMaxText(path: ValidationRecordingPath): string {
            return path.getMax() > 1 ? i18n('field.occurrence.breaks.max.many', path.getMax()) : i18n('field.occurrence.breaks.max.one');
        }

    }

}
