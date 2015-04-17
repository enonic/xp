module api.util {

    export class Timezone implements api.Equitable {

        private offset: number;

        private location: string;

        constructor(builder: TimezoneBuilder) {
            this.offset = builder.offset;
            this.location = builder.location;
        }

        public getOffset(): number {
            return this.offset;
        }

        getLocation(): string {
            return this.location;
        }

        offsetToString(): string {
            return this.padOffset(this.offset);
        }

        toString(): string {
            return this.offsetToString();
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Timezone)) {
                return false;
            }

            var other = <Timezone>o;

            if (!api.ObjectHelper.stringEquals(this.toString(), other.toString())) {
                return false;
            }

            return true;
        }

        private padOffset(num: number, length: number = 2): string {
            var numAsString = String(num);

            while (numAsString.length < length){
                numAsString = "0" + numAsString;
            }

            return numAsString + ":00";
        }

        static isValidTimezone(s: string): boolean {
            if (StringHelper.isBlank(s)) {
                return false;
            }

           return true;
        }

       static isValidOffset(s: number): boolean {
            if (s > -13 && s < 13) {
                return true;
            }

            return false;
        }

        static fromOffset(s: number): Timezone {
            if (!Timezone.isValidOffset(s)) {
                throw new Error("Passed Timezone ofsset is invalid: " + s);
            }

            return Timezone.create()
                .setOffset(s)
                .build();
        }

        static getLocalTimezone(): Timezone {
            return Timezone.fromOffset(DateHelper.getTZOffset());
        }

        static getZeroOffsetTimezone(): Timezone {
            return Timezone.create().setOffset(0).build();
        }

        public static create(): TimezoneBuilder {
            return new TimezoneBuilder();
        }
    }


    export class TimezoneBuilder {

        offset: number;

        location: string;

        public setOffset(value: number): TimezoneBuilder {
            this.offset = value;
            return this;
        }

        public setLocation(value: string): TimezoneBuilder {
            this.location = value;
            return this;
        }

        public build(): Timezone {
            return new Timezone(this);
        }

        public buildDefault(): Timezone {
            this.offset = 0;
            return new Timezone(this);
        }
    }
}