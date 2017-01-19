module api.aggregation {

    export class BucketView extends api.dom.DivEl {

        private bucket: api.aggregation.Bucket;

        private checkbox: api.ui.Checkbox;

        private parentAggregationView: api.aggregation.AggregationView;

        private selectionChangedListeners: Function[] = [];

        private displayName: string;

        constructor(bucket: api.aggregation.Bucket, parentAggregationView: api.aggregation.AggregationView, select: boolean,
                    displayName?: string) {

            super('aggregation-bucket-view');
            this.bucket = bucket;
            this.parentAggregationView = parentAggregationView;
            this.displayName = displayName;

            this.checkbox = api.ui.Checkbox.create().setLabelText(this.resolveLabelValue()).setChecked(select).build();

            this.checkbox.onValueChanged((event: api.ValueChangedEvent) => {
                this.notifySelectionChanged(eval(event.getOldValue()), eval(event.getNewValue()));
            });
            this.appendChild(this.checkbox);

            this.updateUI();
        }

        private resolveLabelValue(): string {

            if (this.displayName != null) {
                return this.displayName + ' (' + this.bucket.getDocCount() + ')';
            }

            return this.resolveKey() + ' (' + this.bucket.getDocCount() + ')';
        }

        private resolveKey(): string {
            let key = this.bucket.getKey();
            if (key.indexOf(':') > 0) {
                return api.util.StringHelper.capitalize(key.substring(key.indexOf(':') + 1));
            }

            return key;
        }

        setDisplayName(displayName: string) {
            this.displayName = displayName;
            this.updateLabel();
        }

        private updateLabel(): void {
            this.checkbox.setLabel(this.resolveLabelValue());
        }

        getBucket(): api.aggregation.Bucket {
            return this.bucket;
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

            this.updateLabel();

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

        unSelectionChanged(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.selectionChangedListeners = this.selectionChangedListeners
                .filter(function (curr: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
                return curr !== listener;
            });
        }

        onSelectionChanged(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.selectionChangedListeners.push(listener);
        }

    }
}
