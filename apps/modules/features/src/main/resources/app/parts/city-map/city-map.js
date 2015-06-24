var thymeleaf = require('/lib/xp/thymeleaf');
var contentSvc = require('/lib/xp/content');

var view = resolve('city-map.page.html');
var service = require('service.js').service;
var citiesLocation = "/features/Cities"

function handleGet(req) {
    var cityServiceUrl = service.serviceUrl('city');

    var cityName;
    var cityLocation;
    if (req.params.city) {
        var city = getCity(req.params.city);
        if (city) {
            cityName = city.displayName;
            cityLocation = city.data.cityLocation;
        }
    }

    cityName = cityName || "City Name";
    cityLocation = cityLocation || "lat,lon";

    var coordinates = cityLocation.split(",");

    var params = {
        cityServiceUrl: cityServiceUrl,
        defaultCityName: cityName,
        cityLatitude: coordinates[0],
        cityLongitude: coordinates[1]
    };
    var body = thymeleaf.render(view, params);

    function getCity(cityName) {
        var result = contentSvc.get({
            key: citiesLocation + '/' + cityName
        });
        return result;
    }

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;