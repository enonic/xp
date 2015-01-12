module api.content.page.region {

    export class ComponentName implements api.Equitable {

        private static COUNT_DELIMITER: string = "-";

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        public hasCountPostfix(): boolean {

            var countDelimiterIndex = this.value.lastIndexOf(ComponentName.COUNT_DELIMITER);
            return countDelimiterIndex > 0 && countDelimiterIndex <= this.value.length - 2;
        }

        public removeCountPostfix(): ComponentName {

            if (!this.hasCountPostfix()) {
                return this;
            }

            var nameWithoutCountPostfix = this.value.substring(0, this.value.lastIndexOf(ComponentName.COUNT_DELIMITER));
            return new ComponentName(nameWithoutCountPostfix);
        }

        public isDuplicateOf(other: ComponentName): boolean {
            if (this.value == other.value) {
                return true;
            }

            if (!this.hasCountPostfix()) {
                return false;
            }

            var nameWithoutCountPostfix = this.removeCountPostfix();
            return nameWithoutCountPostfix.equals(other);
        }

        public createDuplicate(count: number): ComponentName {

            var newValue = this.value + ComponentName.COUNT_DELIMITER + "" + count;
            return new ComponentName(newValue);
        }

        public toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ComponentName)) {
                return false;
            }

            var other = <ComponentName>o;

            if (!api.ObjectHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }
    }
}