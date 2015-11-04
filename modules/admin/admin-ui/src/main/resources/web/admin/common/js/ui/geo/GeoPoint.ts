module api.ui.geo {

    export class GeoPoint extends api.dom.DivEl {

        private geoLocationInput: api.ui.text.TextInput;

        private validUserInput: boolean;

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor() {
            super("geo-point");

            this.validUserInput = true;
            this.geoLocationInput = new api.ui.text.TextInput();
            this.geoLocationInput.getEl().setAttribute("title", "latitude,longitude").addClass("geo-point-input");

            this.layoutItems();

            this.geoLocationInput.onKeyUp((event: KeyboardEvent) => {

                var typedGeoPoint = this.geoLocationInput.getValue();
                this.validUserInput = api.util.StringHelper.isEmpty(typedGeoPoint) ||
                                      api.util.GeoPoint.isValidString(typedGeoPoint);

                this.updateInputStyling();
                this.notifyValueChanged();
            });
        }

        private layoutItems() {
            this.removeChildren();

            this.appendChild(this.geoLocationInput);
            this.geoLocationInput.setPlaceholder(_i18n('latitude,longitude'));
            return this;
        }

        onValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        private notifyValueChanged() {
            var newValue = this.validUserInput ? this.geoLocationInput.getValue() : null;
            var oldValue = this.geoLocationInput.getOldValue();

            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent)=>void) => {
                listener.call(this, new ValueChangedEvent(oldValue, newValue));
            });
        }


        setGeoPoint(value: api.util.GeoPoint): GeoPoint {
            this.geoLocationInput.setValue("" + value.getLatitude() + "," + value.getLongitude());
            return this;
        }

        getGeoPoint(): api.util.GeoPoint {
            if (api.util.StringHelper.isEmpty(this.geoLocationInput.getValue())) {
                return null;
            }
            return <api.util.GeoPoint>api.util.GeoPoint.fromString(this.geoLocationInput.getValue());

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