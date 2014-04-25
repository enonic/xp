module api.content.page {

    export class ComponentName implements api.Equitable {

        private static COUNT_DELIMITER: string = "-";

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        public isDuplicateOf(other: ComponentName): boolean {
            if (this.value == other.value) {
                return true;
            }

            if (!(this.value.lastIndexOf(ComponentName.COUNT_DELIMITER) > 0)) {
                return false;
            }

            var nameWithoutCountPostfix = this.value.substring(0, this.value.lastIndexOf(ComponentName.COUNT_DELIMITER));
            return nameWithoutCountPostfix == other.toString();
        }

        public createDuplicate(count: number): ComponentName {

            var newValue = this.value + ComponentName.COUNT_DELIMITER + "" + count;
            return new ComponentName(newValue);
        }

        public toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof ComponentName)) {
                return false;
            }

            var other = <ComponentName>o;

            if (!api.EquitableHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }
    }
}