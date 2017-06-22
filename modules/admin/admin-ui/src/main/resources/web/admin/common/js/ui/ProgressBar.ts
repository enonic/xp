module api.ui {

    export class ProgressBar extends api.dom.DivEl {

        private progress: api.dom.DivEl;
        private label: api.dom.DivEl;
        private value: number;

        /**
         * Widget to display progress
         * @param value the initial value (defaults to 0)
         */
        constructor(value?: number, label: string = '') {
            super('progress-bar');
            this.value = value || 0;

            this.progress = new api.dom.DivEl('progress-indicator');
            this.label = new api.dom.DivEl('progress-label');
            this.appendChildren(this.progress, this.label);
            this.setValue(this.value);
            this.setLabel(label);
        }

        setLabel(label: string) {
            this.label.setHtml(label);
        }

        setValue(value: number) {
            const normalizedValue = this.isIntAndInRangeOf100(value) ? value / 100 : (value > 0 ? this.normalizeValue(value) : 0);
            this.progress.getEl().setWidth(normalizedValue * 100 + '%');
            this.value = normalizedValue;
        }

        getValue(): number {
            return this.value;
        }

        isComplete(): boolean {
            return this.value >= 1;
        }

        /**
         * Normalizes any value to be in 0-1 interval
         * @param value value to normalize
         * @returns {number} normalized value
         */
        private normalizeValue(value: number) {
            let integralLength = Math.ceil(Math.log(value) / Math.log(10));
            let maxValue = Math.pow(10, integralLength);
            return value / maxValue;
        }

        private isInt(value: number): boolean {
            return value % 1 === 0;
        }

        private isIntAndInRangeOf100(value: number) {
            return this.isInt(value) && value > 0 && value <= 100;
        }

    }

}
