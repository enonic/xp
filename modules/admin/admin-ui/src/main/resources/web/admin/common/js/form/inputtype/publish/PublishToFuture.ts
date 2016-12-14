module api.content.form.inputtype.publish {

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class PublishToFuture extends api.content.form.inputtype.time.DateTime {


        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            if (recording.isValid()) {
                var publishInfoPropertySet: api.data.PropertySet = this.propertyArray.getParent();
                var publishFrom = publishInfoPropertySet.getDateTime("from");
                var publishTo = publishInfoPropertySet.getDateTime("to");

                if (publishTo) {
                    if (publishTo.toDate() < new Date()) {
                        recording.setBreaksMinimumOccurrences(true);
                        recording.setAdditionalValidationRecord(
                            api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                                "[Publish to] value cannot be set in the past").build());
                    } else if (publishFrom && (publishTo.toDate() < publishFrom.toDate())) {
                        recording.setBreaksMinimumOccurrences(true);
                        recording.setAdditionalValidationRecord(
                            api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                                "[Publish to] value must be set after [Publish from]").build());
                    }
                }
            }
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName("PublishToFuture", false);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("PublishToFuture", PublishToFuture));

}