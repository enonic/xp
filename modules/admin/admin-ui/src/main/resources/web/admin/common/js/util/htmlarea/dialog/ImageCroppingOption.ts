module api.util.htmlarea.dialog {

    import i18n = api.util.i18n;

    export class ImageCroppingOption {

        private name: string;

        private displayValue: string;

        private widthProportion: number;

        private heightProportion: number;

        constructor(name: string, widthProportion: number, heightProportion: number, displayValue?: string) {
            this.name = name;
            this.widthProportion = widthProportion;
            this.heightProportion = heightProportion;
            this.displayValue = !!displayValue ? displayValue : this.makeDisplayValue();
        }

        getName(): string {
            return this.name;
        }

        getDisplayValue(): string {
            return this.displayValue;
        }

        setDisplayValue(value: string) {
            this.displayValue = value;
        }

        getProportionString(): string {
            return this.widthProportion + ':' + this.heightProportion;
        }

        private makeDisplayValue(): string {
            return i18n('dialog.image.cropping.' + this.name) + ' (' + this.widthProportion + ':' + this.heightProportion + ')';
        }

    }
}
