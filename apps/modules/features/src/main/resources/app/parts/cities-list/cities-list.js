var portal = require('/lib/xp/portal');
var thymeleaf = require('/lib/view/thymeleaf');
var view = resolve('cities-list.page.html');
var service = require('service.js').service;

function handleGet(req) {

    var currentCityName;
    var cities;

    if (req.params.city) {
        var city = getCity(req.params.city);
        if (city) {
            currentCityName = city.displayName;
            cities = execute('content.query', {
                    start: 0,
                    count: 25,
                    contentTypes: [
                        module.name + ':city'
                    ],
                    "sort": "geoDistance('data.cityLocation','" + city.data.cityLocation + "')",
                    "query": "_name != '" + currentCityName + "'"
                }
            );
        }
    }

    if (!currentCityName) {
        currentCityName = "Select";
    }

    if (!cities) {
        cities = execute('content.query', {
                start: 0,
                count: 25,
                contentTypes: [
                    module.name + ':city'
                ]
            }
        );
    }

    var content = portal.getContent();
    var currentPage = portal.pageUrl({
        path: content._path
    });

    var params = {
        cities: cities.contents,
        currentCity: currentCityName,
        currentPage: currentPage
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