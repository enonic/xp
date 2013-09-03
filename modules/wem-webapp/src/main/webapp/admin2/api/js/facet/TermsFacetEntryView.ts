module api_facet {

    export class TermsFacetEntryView extends FacetEntryView {

        private entry:TermsFacetEntry;

        private checkbox:api_ui.CheckboxInput;

        private label:api_dom.LabelEl;

        constructor(entry:TermsFacetEntry, parentFacetView:FacetView) {
            super(parentFacetView);
            this.entry = entry;

            this.checkbox = new api_ui.CheckboxInput();
            this.checkbox.addListener({
                onValueChanged: (oldValue:boolean, newValue:boolean) => {
                    this.notifySelectionChanged(oldValue, newValue);
                }
            });
            this.appendChild(this.checkbox);

            this.label = new api_dom.LabelEl(this.resolveLabelValue(), this.checkbox);
            this.appendChild(this.label);
            this.label.getEl().addEventListener('click', () => {
                this.checkbox.setChecked(!this.checkbox.isChecked());
            });

            this.updateUI();
        }

        private resolveLabelValue():string {
            return this.entry.getDisplayName() + ' (' + this.entry.getCount() + ')';
        }

        getName():string {
            return this.entry.getName();
        }

        update(entry:TermsFacetEntry) {

            this.entry = entry;
            this.updateUI();
        }

        isSelected():boolean {
            return this.checkbox.isChecked();
        }

        deselect() {
            this.checkbox.setChecked(false);
        }

        private updateUI() {

            this.label.setValue(this.resolveLabelValue());

            if (this.entry.getCount() > 0 || this.isSelected()) {
                this.show();
            } else {
                this.hide();
            }
        }
    }
}