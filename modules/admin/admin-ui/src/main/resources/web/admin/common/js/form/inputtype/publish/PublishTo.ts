module api.content.form.inputtype.publish {

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalDateTime]].
     */
    export class PublishTo extends api.content.form.inputtype.time.DateTime {

        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            if (recording.isValid()) {
                let publishInfoPropertySet: api.data.PropertySet = this.propertyArray.getParent();
                let publishFrom = publishInfoPropertySet.getDateTime('from');
                let publishTo = publishInfoPropertySet.getDateTime('to');
                if (publishTo && publishFrom && (publishTo.toDate() < publishFrom.toDate())) {
                    recording.setBreaksMinimumOccurrences(true);
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            '"Online to" date/time must be later than "Online from"').build());

                }
            }
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName('PublishTo', false);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class('PublishTo', PublishTo));

}
