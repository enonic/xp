module api.ui {

    export class ResponsiveRange {

        private minRange: number;
        private maxRange: number;
        private rangeClass: string;

        constructor(minRange:number, maxRange?:number, rangeClass?:string) {
            this.minRange = minRange;
            this.maxRange = maxRange || 0;
            this.rangeClass = rangeClass || ("_" + minRange + "-" + maxRange);
        }

        getMinimumRange(): number {
            return this.minRange;
        }

        getMaximumRange(): number {
            return this.maxRange;
        }

        getRangeClass(): string {
            return this.rangeClass;
        }

        isFitt(size:number): boolean {
            return (this.minRange <= size) && (size <= this.maxRange);
        }
    }
}
