function handlePost(req) {

    var parentPath = req.formParams.parentPath;
    var cityName = req.formParams.cityName;
    var cityLocation = req.formParams.cityLocation;

    if (cityName && cityLocation) {
        var city = getCity(cityName);

        if (city) {
            modifyCity(city, cityName, cityLocation)
        } else {
            createCity(cityName, cityLocation);
        }
    }

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

    function modifyCity(city, cityName, cityLocation) {

        var result = execute('content.modify', {
            key: city._id,
            editor: function (c) {
                c.data.cityLocation = cityLocation;
                return c;
            }
        });

        return result;
    }

    function createCity(cityName, cityLocation) {
        var result = execute('content.create', {
            name: cityName,
            parentPath: parentPath,
            displayName: cityName,
            draft: false,
            requireValid: true,
            contentType: module.name + ':city',
            data: {
                cityLocation: cityLocation
            }
        });

        return result;
    }

    return {
        redirect: execute('portal.pageUrl', {
            path: parentPath + "?city=" + cityName
        })
    }
}

exports.post = handlePost;