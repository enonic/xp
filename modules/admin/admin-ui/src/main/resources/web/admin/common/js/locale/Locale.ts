module api.locale {

    export class Locale implements api.Equitable {

        private tag: string;
        private displayName: string;
        private language: string;
        private displayLanguage: string;
        private variant: string;
        private displayVariant: string;
        private country: string;
        private displayCountry: string;

        constructor() {

        }

        equals(other: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(other, Locale)) {
                return false;
            }
            var o = <Locale> other;
            return this.tag == o.tag &&
                   this.displayName == o.displayName &&
                   this.language == o.language &&
                   this.displayLanguage == o.displayLanguage &&
                   this.variant == o.variant &&
                   this.displayVariant == o.displayVariant &&
                   this.country == o.displayCountry &&
                   this.displayCountry == o.displayCountry;
        }

        public static fromJson(json: api.locale.json.LocaleJson): Locale {
            var l = new Locale();
            l.tag = json.tag;
            l.displayName = json.displayName;
            l.country = json.country;
            l.displayCountry = json.displayCountry;
            l.variant = json.variant;
            l.displayVariant = json.displayVariant;
            l.language = json.language;
            l.displayLanguage = json.displayLanguage;
            return l;
        }

        public getTag() {
            return this.tag;
        }

        public getDisplayName() {
            return this.displayName;
        }

        public getLanguage() {
            return this.language;
        }

        public getDisplayLanguage() {
            return this.displayLanguage;
        }

        public getVariant() {
            return this.variant;
        }

        public getDisplayVariant() {
            return this.displayVariant;
        }

        public getCountry() {
            return this.country;
        }

        public getDisplayCountry() {
            return this.displayCountry;
        }

    }

}