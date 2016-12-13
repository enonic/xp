module api.content.form.inputtype.publish {

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class PublishFrom extends api.content.form.inputtype.time.DateTime {


        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            if (recording.isValid()) {
                var publishInfoPropertySet: api.data.PropertySet = this.propertyArray.getParent();
                var publishFrom = publishInfoPropertySet.getDateTime("from");
                var publishTo = publishInfoPropertySet.getDateTime("to");

                if (!publishFrom && publishTo) {
                    recording.setBreaksMinimumOccurrences(true);
                    recording.setAdditionalValidationRecord(api.form.AdditionalValidationRecord.create().
                        setOverwriteDefault(true).
                        setMessage("[Publish to] cannot be set without [Publish from]").
                        build());
                } else if (publishFrom && publishTo && (publishTo.toDate() < publishFrom.toDate())) {
                    recording.setBreaksMinimumOccurrences(true);
                    recording.setAdditionalValidationRecord(api.form.AdditionalValidationRecord.create().
                        setOverwriteDefault(true).
                        setMessage("[Publish from] value must be set before [Publish to]").
                        build());
                }
            }
        }


        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName("PublishFrom", false);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("PublishFrom", PublishFrom));

}