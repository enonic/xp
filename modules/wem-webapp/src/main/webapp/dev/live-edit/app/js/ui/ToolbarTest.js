AdminLiveEdit.ToolbarTest = (function () {
    'use strict';

    var possibleLocations = [
        {
            height: 60,
            width: '',
            top: 0,
            right: 0,
            bottom: '',
            left: 0
        },
        {
            height: '',
            width: 60,
            top: 0,
            right: 0,
            bottom: 0,
            left: ''
        },
        {
            height: 60,
            width: '',
            top: '',
            right: 0,
            bottom: 0,
            left: 0
        },
        {
            height: '',
            width: 60,
            top: 0,
            right: '',
            bottom: 0,
            left: 0
        }
    ];

    var currentLocation = 0;

    function updateLocation() {
        getToolbar().css(possibleLocations[currentLocation]);
        currentLocation++;
        if (currentLocation === possibleLocations.length) {
            currentLocation = 0;
        }
    }

    function create() {
        var $toolbar = $liveedit('<div id="live-edit-toolbar-test" style="background-color: #deefff; position: fixed; padding: 5px; z-index: 3000000; cursor: default; text-align: right"></div>');
        var $toggleLocationButton = $liveedit('<button>Toggle location</button>');

        $toolbar.on('click mouseover mouseenter mousemove', function(event) {
            event.stopPropagation();
        });

        $toggleLocationButton.on('click', function (event) {
            event.stopPropagation();
            updateLocation();
        });

        $toolbar.append($toggleLocationButton);
        $liveedit('body').append($toolbar);

        updateLocation();
    }

    function getToolbar() {
        return $liveedit('#live-edit-toolbar-test');
    }

    create();

}());



