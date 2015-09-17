module api.ui.time {

    export class SelectedDateChangedEvent {

        private date: Date;

        constructor(selectedDate: Date) {
            this.date = selectedDate;
        }

        getDate(): Date {
            return this.date;
        }
    }


}
