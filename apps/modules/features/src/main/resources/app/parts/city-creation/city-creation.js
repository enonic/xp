var portal = require('/lib/xp/portal');
var thymeleaf = require('/lib/xp/thymeleaf');
var view = resolve('city-creation.page.html');
var service = require('service.js').service;

function handleGet(req) {
    var cityServiceUrl = service.serviceUrl('city');
    var content = portal.getContent();

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

    var params = {
        cityServiceUrl: cityServiceUrl,
        parentPath: content._path,
        defaultCityName: cityName,
        defaultCityLocation: cityLocation
    };
    var body = thymeleaf.render(view, params);

    function getCity(cityName) {
        var result = execute('content.query', {
                count: 1,
                contentTypes: [
                    module.name + ':city'
                ],
                "query": "_name = '" + cityName + "'"
            }
        );

        return result.contents[0];
    }

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;