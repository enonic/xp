module api_facet {

    export class QueryFacetEntryView extends FacetEntryView {

        private queryFacet:QueryFacet;

        private checkbox:api_ui.CheckboxInput;

        private label:api_dom.LabelEl;

        constructor(queryFacet:QueryFacet, parentFacetView:QueryFacetView) {
            super(parentFacetView);
            this.queryFacet = queryFacet;

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
            return this.queryFacet.getName() + ' (' + this.queryFacet.getCount() + ')';
        }

        getName():string {
            return this.queryFacet.getName();
        }

        update(facet:QueryFacet) {
            this.queryFacet = facet;
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

            if (this.queryFacet.getCount() > 0 || this.isSelected()) {
                this.show();
            } else {
                this.hide();
            }
        }
    }
}