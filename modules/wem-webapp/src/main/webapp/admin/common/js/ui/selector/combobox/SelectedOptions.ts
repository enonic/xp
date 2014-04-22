module api.ui.selector.combobox {

    export class SelectedOptions<T>{

        private list:SelectedOption<T>[] = [];

        count():number {
            return this.list.length;
        }

        getSelectedOption(index:number):SelectedOption<T> {
            return this.list.filter((selectedOption: SelectedOption<T>) => {
                return selectedOption.getIndex() == index;
            })[0];
        }

        getOptions():api.ui.selector.Option<T>[] {
            return this.list.sort((option1: SelectedOption<T>, option2: SelectedOption<T>) => {
                return option1.getIndex() - option2.getIndex();
            }).map( (selectedOption:SelectedOption<T>) => selectedOption.getOption());
        }

        getOptionViews():SelectedOptionView<T>[] {
            return this.list.sort((option1: SelectedOption<T>, option2: SelectedOption<T>) => {
                return option1.getIndex() - option2.getIndex();
            }).map( (selectedOption:SelectedOption<T>) => selectedOption.getOptionView());
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
    }
}

