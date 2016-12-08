module api.content.form.inputtype.publish {

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class PublishTo extends api.content.form.inputtype.time.DateTime {


        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            if (recording.isValid()) {
                var publishInfoPropertySet: api.data.PropertySet = this.propertyArray.getParent();
                var publishFrom = publishInfoPropertySet.getDateTime("publishFrom");
                var publishTo = publishInfoPropertySet.getDateTime("publishTo");

                if (publishTo && (publishTo.toDate() < new Date())) {
                    recording.setAdditionalValidationRecord(api.form.AdditionalValidationRecord.create().
                        setOverwriteDefault(true).
                        setMessage("Publish to value cannot be set in the past").
                        build());
                }
            }
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName("PublishTo", false);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("PublishTo", PublishTo));

}