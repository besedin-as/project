window.onload = function () {
    window.onscroll = function () {
        var doc = document.body,
            scrollPosition = doc.scrollTop,
            pageSize = (doc.scrollHeight - doc.clientHeight),
            percentageScrolled = Math.floor((scrollPosition / pageSize) * 100);

        if (percentageScrolled >= 50) { // if the percentage is >= 50, scroll to top
            window.scrollTo(0, 0);
        }

        console.log(scrollPosition);
    };
};