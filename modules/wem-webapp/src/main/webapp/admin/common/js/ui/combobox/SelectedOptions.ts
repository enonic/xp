module api_ui_combobox {

    export class SelectedOptions<T>{

        private list:SelectedOption<T>[] = [];

        count():number {
            return this.list.length;
        }

        getSelectedOption(index:number):SelectedOption<T> {
            return this.list[index];
        }

        getOptions():OptionData<T>[] {
            var options:OptionData<T>[] = [];
            this.list.forEach( (selectedOption:SelectedOption<T>) => {
                options.push(selectedOption.getOption());
            } );
            return options;
        }

        getOptionViews():ComboBoxSelectedOptionView<T>[] {
            var options:ComboBoxSelectedOptionView<T>[] = [];
            this.list.forEach( (selectedOption:SelectedOption<T>) => {
                options.push(selectedOption.getOptionView());
            } );
            return options;
        }

        add(selectedOption:SelectedOption<T>) {
            this.list.push(selectedOption);
        }

        getByView(view:ComboBoxSelectedOptionView<T>) {

            for(var i = 0; i < this.list.length; i++) {
                if( this.list[i].getOptionView() == view ) {
                    return this.list[i];
                }
            }
            return null;
        }

        getByOption(option:OptionData<T>):SelectedOption<T> {

            for(var i = 0; i < this.list.length; i++) {
                if( this.list[i].getOption() == option ) {
                    return this.list[i];
                }
            }
            return null;
        }

        remove(selectedOption:SelectedOption<T>) {

            this.list = this.list.filter( (option:SelectedOption<T>) => {
                return option.getOption().value != selectedOption.getOption().value;
            });
        }
    }
}

