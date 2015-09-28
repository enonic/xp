module api.form.inputtype.combobox {

    export class ComboBoxDisplayValueViewer extends api.ui.Viewer<string> {

        constructor() {
            super();
        }

        setObject(value: string) {
            super.setObject(value);
            this.getEl().setInnerHtml(value);
        }

        getPreferredHeight(): number {
            return 34;
        }

    }
}