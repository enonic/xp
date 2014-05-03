module api.ui.selector.combobox {

    export class SelectedOptions<T>{

        private list:SelectedOption<T>[] = [];

        count():number {
            return this.list.length;
        }

        getSelectedOption(index:number):SelectedOption<T> {
            return this.list[index];
        }

        getOptions():api.ui.selector.Option<T>[] {
            return this.list.map( (selectedOption:SelectedOption<T>) => selectedOption.getOption() );
        }

        getOptionViews():SelectedOptionView<T>[] {
            return this.list.map( (selectedOption:SelectedOption<T>) => selectedOption.getOptionView() );
        }

        add(selectedOption:SelectedOption<T>) {
            this.list.push(selectedOption);
        }

        getByView(view:SelectedOptionView<T>): SelectedOption<T> {
            return this.list.filter((selectedOption: SelectedOption<T>) => {
                return selectedOption.getOptionView() == view;
            })[0];
        }

        getByOption(option:api.ui.selector.Option<T>):SelectedOption<T> {
            return this.list.filter((selectedOption: SelectedOption<T>) => {
                return selectedOption.getOption() == option;
            })[0];
        }

        remove(selectedOption:SelectedOption<T>) {

            this.list = this.list.filter( (option:SelectedOption<T>) => {
                return option.getOption().value != selectedOption.getOption().value;
            });
        }

        moveOccurrence(formIndex: number, toIndex: number) {

            api.util.ArrayHelper.moveElement(formIndex, toIndex, this.list);

            this.list.forEach((selectedOption: SelectedOption<T>, index: number) => selectedOption.setIndex(index));
        }
    }
}

