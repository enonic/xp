module api.form {

    export class AdditionalValidationRecord {

        private message: string;
        private overwriteDefault: boolean = false;

        constructor(builder: Builder) {
            this.message = builder.message;
            this.overwriteDefault = builder.overwriteDefault;
        }

        public static create(): Builder {
            return new Builder();
        }

        getMessage(): string {
            return this.message;
        }

        isOverwriteDefault(): boolean {
            return this.overwriteDefault;
        }

        equals(that: AdditionalValidationRecord): boolean {

            if (this.message != that.message) {
                return false;
            }

            if (this.overwriteDefault != that.overwriteDefault) {
                return false;
            }

            return true;
        }
    }

    export class Builder {

        message: string;
        overwriteDefault: boolean = false;

        setMessage(value: string): Builder {
            this.message = value;
            return this;
        }

        setOverwriteDefault(value: boolean): Builder {
            this.overwriteDefault = value;
            return this;
        }

        build(): AdditionalValidationRecord {
            return new AdditionalValidationRecord(this);
        }
    }

}