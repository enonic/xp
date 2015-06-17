$(function () {

    $('form input[type="submit"]').on('click', function (e) {
        e.preventDefault();

        var form = $(this).closest('form');

        $.ajax({
            url: form.attr('action'),
            method: "POST",
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            dataType: 'json',
            data: {
                scale: $('input[name="scale"]', form).val(),
                filter: $('input[name="filter"]', form).val()
            }
        }).done(function (resp) {
            var container = form.parent().parent();
            var parent = $('.fp-images', container);

            var imageElements = resp.images.map(function (imageUrl) {
                var figure = $('<figure/>');
                var img = $('<img />').attr('src', imageUrl);
                var caption = $('<figcaption/>');
                var a = $('<a target="_blank"/>').attr('href', imageUrl).text(imageUrl);
                caption.append(document.createTextNode('URL ')).append(a);
                figure.append(img).append(caption);
                return figure;
            });

            parent.fadeOut(250, function () {
                parent.empty().show().append(imageElements);
            });
        });
    });

    $('select[name="scaleSelect"]').on('change', function (e) {
        var value = $(this).children(':selected').val();
        var form = $(this).closest('form');
        $('input[name="scale"]', form).val(value);
        $('input[type="submit"]', form).click();
    });

    $('select[name="scaleSelect"]').each(function () {
        var select = $(this);
        var form = select.closest('form');
        var value = $(this).find('option:first').val();
        $('input[name="scale"]', form).val(value);
    });

    $('select[name="filterSelect"]').on('change', function (e) {
        var value = $(this).children(':selected').val();
        var form = $(this).closest('form');
        $('input[name="filter"]', form).val(value);
        $('input[type="submit"]', form).click();
    });

});