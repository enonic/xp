module api.content.form.inputtype.publish {

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class PublishToFuture extends api.content.form.inputtype.time.DateTime {


        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            if (recording.isValid()) {
                let publishInfoPropertySet: api.data.PropertySet = this.propertyArray.getParent();
                let publishFrom = publishInfoPropertySet.getDateTime("from");
                let publishTo = publishInfoPropertySet.getDateTime("to");

                if (publishTo) {
                    if (publishTo.toDate() < new Date()) {
                        recording.setBreaksMinimumOccurrences(true);
                        recording.setAdditionalValidationRecord(
                            api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                                '"Online to" date/time cannot be in the past').build());
                    } else if (publishFrom && (publishTo.toDate() < publishFrom.toDate())) {
                        recording.setBreaksMinimumOccurrences(true);
                        recording.setAdditionalValidationRecord(
                            api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                                '"Online to" date/time value must be later than "Publish from"').build());
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