module api.content.form.inputtype.geo {

    import support = api.form.inputtype.support;

    import ValueTypes = api.data.type.ValueTypes;

    export class GeoPoint extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return ValueTypes.GEO_POINT;
        }

        newInitialValue(): string {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var geoPoint = new api.ui.geo.GeoPoint();

            if (property.hasNonNullValue()) {
                var geoPointValue = property.getGeoPoint();
                if (geoPointValue) {
                    geoPoint.setGeoPoint(geoPointValue);
                }
            }
            return geoPoint;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var geoPoint = <api.ui.geo.GeoPoint>element;

            geoPoint.onLatitudeChanged((event: api.ui.ValueChangedEvent) => {

                var latitude = event.getNewValue();
                var longitude = geoPoint.getLongitude();
                var geoPointAsString = latitude + "," + longitude;
                var value = ValueTypes.GEO_POINT.newValue(geoPointAsString);
                listener(new api.form.inputtype.support.ValueChangedEvent(value));
            });

            geoPoint.onLongitudeChanged((event: api.ui.ValueChangedEvent) => {
                var latitude = geoPoint.getLatitude();
                var longitude = event.getNewValue();
                var geoPointAsString = latitude + "," + longitude;
                var value = ValueTypes.GEO_POINT.newValue(geoPointAsString);
                listener(new api.form.inputtype.support.ValueChangedEvent(value));
            });
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var geoPoint: api.ui.geo.GeoPoint = <api.ui.geo.GeoPoint>occurrence;
            var latitude = geoPoint.getLatitude();
            var longitude = geoPoint.getLongitude();
            return ValueTypes.GEO_POINT.newValue(latitude + "," + longitude);
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            return !value.getType().equals(ValueTypes.LOCAL_DATE);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("GeoPoint", GeoPoint));
}