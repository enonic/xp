module api.content.form.inputtype.publish {

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class PublishFrom extends api.content.form.inputtype.time.DateTime {

        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            if (recording.isValid()) {
                let publishInfoPropertySet: api.data.PropertySet = this.propertyArray.getParent();
                let publishFrom = publishInfoPropertySet.getDateTime('from');
                let publishTo = publishInfoPropertySet.getDateTime('to');

                if (!publishFrom && publishTo) {
                    recording.setBreaksMinimumOccurrences(true);
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            '"Online to" date/time cannot be set without "Online from"').build());
                } else if (publishFrom && publishTo && (publishTo.toDate() < publishFrom.toDate())) {
                    recording.setBreaksMinimumOccurrences(true);
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            '"Online from" date/time must be earlier than "Online to"').build());
                }
            }
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName('PublishFrom', false);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class('PublishFrom', PublishFrom));

}
