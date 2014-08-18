module api.ui.time {

    export class MonthOfYear implements api.Equitable {

        private numberCode: number;

        private oneLetterName;

        private shortName;

        private fullName;

        private previous: MonthOfYear;

        private next: MonthOfYear;

        constructor(numberCode: number, oneLetterName: string, shortName: string, fullName: string) {
            this.numberCode = numberCode;
            this.oneLetterName = oneLetterName;
            this.shortName = shortName;
            this.fullName = fullName;
            //this.previous = previoius;
            //this.next = next;
        }

        getNumberCode(): number {
            return this.numberCode;
        }

        getOneLetterName(): string {
            return this.oneLetterName;
        }

        getShortName(): string {
            return this.shortName;
        }

        getFullName(): string {
            return this.fullName;
        }

        getPrevioius(): MonthOfYear {
            return this.previous;
        }

        getNext(): MonthOfYear {
            return this.next;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MonthOfYear)) {
                return false;
            }

            var other = <MonthOfYear>o;

            if (!api.ObjectHelper.numberEquals(this.numberCode, other.numberCode)) {
                return false;
            }

            return true;
        }
    }
}
