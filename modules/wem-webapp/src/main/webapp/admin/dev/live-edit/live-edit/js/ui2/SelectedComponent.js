(function () {
    // Class definition (constructor function)
    var selectedComponent = AdminLiveEdit.ui.SelectedComponent = function () {
    };

    // Inherits ui.Base
    selectedComponent.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    selectedComponent.constructor = selectedComponent;

    // Shorthand ref to the prototype
    var p = selectedComponent.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());