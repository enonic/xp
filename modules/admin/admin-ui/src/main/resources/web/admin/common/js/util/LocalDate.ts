module api.util {

    import StringHelper = api.util.StringHelper;

    export class LocalDate implements api.Equitable {

        public static DATE_SEPARATOR: string = "-";

        private year: number;

        private month: number;

        private day: number;

        constructor(builder: LocalDateBuilder) {
            this.year = builder.year;
            this.month = builder.month;
            this.day = builder.day;
        }

        getYear(): number {
            return this.year;
        }

        getMonth(): number {
            return this.month;
        }

        getDay(): number {
            return this.day;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LocalDate)) {
                return false;
            }
            var other = <LocalDate>o;
            if (!api.ObjectHelper.stringEquals(this.toString(), other.toString())) {
                return false;
            }
            return true;
        }

        toString(): string {
            return this.year + LocalDate.DATE_SEPARATOR + this.padNumber(this.month) + LocalDate.DATE_SEPARATOR +
                   this.padNumber(this.day);
        }

        toDate(): Date {
            return DateHelper.parseDate(this.toString(), LocalDate.DATE_SEPARATOR);
        }

        private padNumber(num: number, length: number = 2): string {
            var numAsString = String(num);

            while (numAsString.length < length) {
                numAsString = "0" + numAsString;
            }
            return numAsString;
        }


        static isValidDate(s: string): boolean {
            return !!api.util.DateHelper.parseDate(s);
        }

        static fromDate(date: Date) {
            if (date) {
                return LocalDate.parseDate(date.getFullYear() + LocalDate.DATE_SEPARATOR + (date.getMonth() + 1) +
                                           LocalDate.DATE_SEPARATOR + date.getDate(), false);
            }
        }

        static parseDate(s: string, isNeedToCheck: boolean = true): LocalDate {
            if (isNeedToCheck && !LocalDate.isValidDate(s)) {
                throw new Error("Cannot parse LocalDate from string: " + s);
            }
            var date: string[] = s.split(LocalDate.DATE_SEPARATOR);
            return LocalDate.create().
                setYear(Number(date[0])).
                setMonth(Number(date[1])).
                setDay(Number(date[2])).
                build();
        }

        public static create(): LocalDateBuilder {
            return new LocalDateBuilder();
        }

    }

    export class LocalDateBuilder {
        year: number;

        month: number;

        day: number;

        setYear(value: number): LocalDateBuilder {
            this.year = value;
            return this;
        }

        setMonth(value: number): LocalDateBuilder {
            this.month = value;
            return this;
        }

        setDay(value: number): LocalDateBuilder {
            this.day = value;
            return this;
        }

        validate() {
            if (!this.year) {
                throw new Error("Invalid parameter. Year should be set");
            } else if (!this.month) {
                throw new Error("Invalid parameter. Month should be set");
            } else if (!this.day) {
                throw new Error("Invalid parameter. Day should be set");
            }
        }


        public build(): LocalDate {
            this.validate();
            return new LocalDate(this);
        }

    }

}