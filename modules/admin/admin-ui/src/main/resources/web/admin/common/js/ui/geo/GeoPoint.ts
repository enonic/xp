module api.ui.geo {

    export class GeoPoint extends api.ui.text.TextInput {

        private validUserInput: boolean;

        constructor(originalValue?: api.util.GeoPoint) {
            super("geo-point", undefined, originalValue ? originalValue.toString() : undefined);

            this.validUserInput = true;
            this.getEl().setAttribute("title", "latitude,longitude");
            this.setPlaceholder(_i18n('latitude,longitude'));

            this.onValueChanged((event: api.ValueChangedEvent) => {
                var typedGeoPoint = this.getValue();
                this.validUserInput = api.util.StringHelper.isEmpty(typedGeoPoint) ||
                                      api.util.GeoPoint.isValidString(typedGeoPoint);

                this.updateValidationStatusOnUserInput(this.validUserInput);
            });
        }

        setGeoPoint(value: api.util.GeoPoint): GeoPoint {
            this.setValue(value ? value.toString() : "");
            return this;
        }

        getGeoPoint(): api.util.GeoPoint {
            var value = this.getValue();
            if (api.util.StringHelper.isEmpty(value)) {
                return null;
            }
            return <api.util.GeoPoint> api.util.GeoPoint.fromString(value);
        }

        hasValidUserInput(): boolean {
            return this.validUserInput;
        }

    }
}