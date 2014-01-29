module api.aggregation {

    export class BucketView extends api.dom.DivEl {

        private bucket: api.aggregation.Bucket;

        private checkbox: api.ui.CheckboxInput;

        private label: api.dom.LabelEl;

        private parentAggregationView: api.aggregation.AggregationView;

        constructor(bucket: api.aggregation.Bucket, parentAggregationView: api.aggregation.AggregationView) {

            super('facet-entry-view');
            this.bucket = bucket;
            this.parentAggregationView = parentAggregationView;

            this.checkbox = new api.ui.CheckboxInput();
            this.checkbox.addListener({
                onValueChanged: (oldValue: boolean, newValue: boolean) => {
                    this.notifySelectionChanged(oldValue, newValue);
                }
            });
            this.appendChild(this.checkbox);

            this.label = new api.dom.LabelEl(this.resolveLabelValue(), this.checkbox);
            this.appendChild(this.label);
            this.label.getEl().addEventListener('click', () => {
                this.checkbox.setChecked(!this.checkbox.isChecked());
            });

            this.updateUI();
        }

        private resolveLabelValue(): string {
            return this.bucket.getName() + ' (' + this.bucket.getDocCount() + ')';
        }


        getParentAggregationView() {
            return this.parentAggregationView;
        }

        isSelected(): boolean {
            return this.checkbox.isChecked();
        }

        private updateUI() {

            this.label.setValue(this.resolveLabelValue());

            if (this.bucket.getDocCount() > 0 || this.isSelected()) {
                this.show();
            } else {
                this.hide();
            }
        }

        notifySelectionChanged(oldValue: boolean, newValue: boolean) {

            //   this.selectionChangedListeners.forEach((listener:(event:FacetEntryViewSelectionChangedEvent) => void) => {
            //       listener(new FacetEntryViewSelectionChangedEvent(oldValue, newValue, this));
            //   });
        }
    }
}