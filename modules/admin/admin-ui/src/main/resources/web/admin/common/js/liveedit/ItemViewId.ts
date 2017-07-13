module api.liveedit {

    import i18n = api.util.i18n;
    export class ItemViewId implements api.Equitable {

        static DATA_ATTRIBUTE: string = 'live-edit-id';

        private value: number;

        private refString: string;

        constructor(value: number) {
            api.util.assert(value >= 1, i18n('live.view.itemviewid.istooshort'));
            this.value = value;
            this.refString = '' + value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ItemViewId)) {
                return false;
            }

            let other = <ItemViewId>o;

            if (!api.ObjectHelper.numberEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        toNumber(): number {
            return this.value;
        }

        toString(): string {
            return this.refString;
        }

        static fromString(s: string) {
            return new ItemViewId(+s);
        }
    }
}
