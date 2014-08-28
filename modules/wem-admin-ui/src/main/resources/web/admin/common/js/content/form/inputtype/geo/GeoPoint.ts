module api.content.form.inputtype.geo {

    import support = api.form.inputtype.support;

    export class GeoPoint extends support.BaseInputTypeNotManagingAdd<any> {

        private geoPoint: api.ui.geo.GeoPoint;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {

            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            this.geoPoint = new api.ui.geo.GeoPoint();

            if (property != null) {
                var geoString: string = property.getValue().asString();
                if (!api.util.isStringEmpty(geoString)) {
                    var values: string[] = geoString.split(',');
                    this.geoPoint.setLatitude(Number(values[0]).toString());
                    this.geoPoint.setLongitude(Number(values[1]).toString());
                }

            }
            else {
                this.geoPoint.setLatitude("");
                this.geoPoint.setLongitude("");
            }
            return this.geoPoint;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            this.geoPoint.onLatitudeChanged((event: api.ui.ValueChangedEvent) => {
                var newLatitude = event.getNewValue();
                var newGeoPoint: string = [ newLatitude, this.geoPoint.getLongitude()].join();
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(newGeoPoint)));
            });

            this.geoPoint.onLongitudeChanged((event: api.ui.ValueChangedEvent) => {
                var newLongitude = event.getNewValue();
                var newGeoPoint: string = [ this.geoPoint.getLatitude(), newLongitude].join();
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(newGeoPoint)));
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var geoPoint: api.ui.geo.GeoPoint = <api.ui.geo.GeoPoint>occurrence;
            var newValue: string = [ geoPoint.getLatitude(), geoPoint.getLongitude()].join();
            return this.newValue(newValue);
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("GeoPoint", GeoPoint));
}