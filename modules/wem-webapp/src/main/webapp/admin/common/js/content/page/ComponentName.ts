module api.content.page {

    export class ComponentName {

        private static COUNT_DELIMITER: string = "-";

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        public isDuplicateOf(other: ComponentName): boolean {
            if( this.value == other.value )
            {
                return true;
            }

            if (!(this.value.lastIndexOf(ComponentName.COUNT_DELIMITER) > 0)) {
                return false;
            }

            var nameWithoutCountPostfix = this.value.substring(this.value.lastIndexOf(ComponentName.COUNT_DELIMITER), this.value.length);
            return nameWithoutCountPostfix == other.toString();
        }

        public createDuplicate(count: number): ComponentName {

            var newValue = this.value + ComponentName.COUNT_DELIMITER + "" + count;
            return new ComponentName(newValue);
        }

        public toString(): string {
            return this.value.toString();
        }
    }
}