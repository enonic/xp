module api.ui.geo {

    export class GeoPoint extends api.dom.DivEl {

        private geoLocationInput: api.ui.text.TextInput;

        private  validUserInput: boolean;

        constructor() {
            super("geo-point");

            this.validUserInput = true;
            this.geoLocationInput = new api.ui.text.TextInput();
            this.geoLocationInput.getEl().setAttribute("title", "geo location").addClass("geo-point-input");

            this.layoutItems();

            this.geoLocationInput.onKeyUp((event: KeyboardEvent) => {

                var typedGeoPoint = this.geoLocationInput.getValue();
                if (api.util.StringHelper.isEmpty(typedGeoPoint)) {
                    this.validUserInput = true;
                } else {

                    if (api.util.GeoPoint.isValidString(typedGeoPoint)) {
                        this.validUserInput = true;
                    } else {
                        this.validUserInput = false;
                    }
                }

                this.updateInputStyling();
            });
        }

        private layoutItems() {
            this.removeChildren();

            this.appendChild(this.geoLocationInput);
            this.geoLocationInput.setPlaceholder(_i18n('geo location'));
            return this;
        }

        onValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.geoLocationInput.onValueChanged(listener);
        }


        setGeoPoint(value: api.util.GeoPoint): GeoPoint {
            this.geoLocationInput.setValue("" + value.getLatitude() + "," + value.getLongitude());
            return this;
        }

        getGeoPoint(): api.util.GeoPoint {
            if (api.util.StringHelper.isEmpty(this.geoLocationInput.getValue())) {
                return null;
            }
            return  api.util.GeoPoint.fromString(this.geoLocationInput.getValue());

        }

        giveFocus(): boolean {
            return this.geoLocationInput.giveFocus();
        }

        hasValidUserInput(): boolean {
            return this.validUserInput;
        }

        private updateInputStyling() {
            this.geoLocationInput.updateValidationStatusOnUserInput(this.validUserInput);
        }

    }
}