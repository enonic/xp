module api.util.htmlarea.dialog {

    export class ImageCroppingOption {

        private name: string;

        private displayValue: string;

        private widthProportion: number;

        private heightProportion: number;

        constructor(name: string, widthProportion: number, heightProportion: number) {
            this.name = name;
            this.widthProportion = widthProportion;
            this.heightProportion = heightProportion;
            this.displayValue = api.util.StringHelper.capitalize(name) + " (" + widthProportion + ":" + heightProportion + ")";
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
            return this.widthProportion + ":" + this.heightProportion;
        }

    }
}