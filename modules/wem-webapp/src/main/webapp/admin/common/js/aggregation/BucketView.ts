module api.aggregation {

    export class BucketView extends api.dom.DivEl {

        private bucket: api.aggregation.Bucket;

        private checkbox: api.ui.CheckboxInput;

        private label: api.dom.LabelEl;

        private parentAggregationView: api.aggregation.AggregationView;

        private selectionChangedListeners: Function[] = [];

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

            this.updateUI();
        }

        private resolveLabelValue(): string {
            return this.bucket.getKey() + ' (' + this.bucket.getDocCount() + ')';
        }

        public getSelectedValue(): string {

            return this.getName();
        }

        getName(): string {
            return this.bucket.getKey();
        }

        update(bucket: api.aggregation.Bucket) {
            this.bucket = bucket;
            this.updateUI();
        }

        isSelected(): boolean {
            return this.checkbox.isChecked();
        }

        deselect(supressEvent?: boolean) {
            this.checkbox.setChecked(false, supressEvent);
        }

        private updateUI() {

            this.label.setValue(this.resolveLabelValue());

            if (this.bucket.getDocCount() > 0 || this.isSelected()) {
                this.show();
            } else {
                this.hide();
            }
        }

        getParentAggregationView() {
            return this.parentAggregationView;
        }

        notifySelectionChanged(oldValue: boolean, newValue: boolean) {

            this.selectionChangedListeners.forEach((listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) => {
                listener(new api.aggregation.BucketViewSelectionChangedEvent(oldValue, newValue, this));
            });
        }

        removeSelectionChangedListener(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.selectionChangedListeners = this.selectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        addSelectionChangeListener(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.selectionChangedListeners.push(listener);
        }

    }
}