module api.form.inputtype.combobox {

    export class ComboBoxDisplayValueViewer extends api.ui.Viewer<string> {

        constructor() {
            super();
        }

        setObject(value: string) {
            this.getEl().setInnerHtml(value);

            return super.setObject(value);
        }

        getPreferredHeight(): number {
            return 34;
        }

    }
}
