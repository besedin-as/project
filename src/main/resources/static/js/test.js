$(document).ready(function () {

    window.onload = function () {
        window.onscroll = function () {
            var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
            $.ajax({
                url: "/file",
                type: "POST",
                data: {
                    scrollTop: scrollTop
                },
                success: function () {
                    return true;
                }
            })
        };
    };

    setInterval(function () {
        $.ajax({
            url: "/file/update_position",
            type: "GET",
            success: function (data) {
                window.scrollTo(0, data);
            }
        })
    }, 1000);
});