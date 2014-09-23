module api.util {

    export class GeoPoint {

        private latitude: number;

        private longitude: number;

        constructor(latitude: number, longitude: number) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        getLatitude(): number {
            return this.latitude;
        }

        getLongitude(): number {
            return this.longitude;
        }

        toString(): string {
            return "" + this.latitude + "," + this.longitude;
        }

        static isValidString(s: string): boolean {
            if (StringHelper.isBlank(s)) {
                return false;
            }

            var indexOfComma = s.indexOf(',');
            if (indexOfComma < 1) {
                return false;
            }
            else if (indexOfComma == s.length - 1) {
                return false;
            }

            var coordinates: string[] = s.split(',');

            var latitude = Number(coordinates[0]);
            var longitude = Number(coordinates[1]);

            return typeof latitude === 'number' && !isNaN(latitude) && typeof longitude === 'number' && !isNaN(longitude);
        }

        static fromString(s: string): GeoPoint {
            if (!GeoPoint.isValidString(s)) {
                throw new Error("Cannot parse GeoPoint from string: " + s);
            }
            var coordinates: string[] = s.split(',');
            var latitude = Number(coordinates[0]);
            var longitude = Number(coordinates[1]);
            return new GeoPoint(latitude, longitude);
        }
    }
}