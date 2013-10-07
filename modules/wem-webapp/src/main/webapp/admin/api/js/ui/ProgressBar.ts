module api_ui {

    export class ProgressBar extends api_dom.DivEl {

        private progress:api_dom.DivEl;
        private value:number;

        /**
         * Widget to display progress
         * @param value the initial value (defaults to 0)
         */
            constructor(value?:number) {
            super("ProgressBar", "progress-bar");
            this.value = value || 0;

            var progress = this.progress = new api_dom.DivEl("ProgressBar", "progress-indicator");
            this.getEl().appendChild(progress.getHTMLElement());
            this.setValue(this.value);
        }

        setValue(value:number) {
            var normalizedValue = value > 0 ? this.normalizeValue(value) : 0;
            this.progress.getEl().setWidth(normalizedValue * 100 + "%");
            this.value = normalizedValue;
        }

        getValue():number {
            return this.value;
        }

        /**
         * Normalizes any value to be in 0-1 interval
         * @param value value to normalize
         * @returns {number} normalized value
         */
        private normalizeValue(value:number) {
            var integralLength = Math.ceil(Math.log(value) / Math.log(10));
            var maxValue = Math.pow(10, integralLength);
            return value / maxValue;
        }

    }

}