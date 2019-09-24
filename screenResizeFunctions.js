function positionElements(){
    var widthRatio = screen.width < 760 ? screen.width / 760: 1; 

    console.log("widthRatio: " + widthRatio);
    var eleT, h, w;

    eleT =  $('.layer-4');
    h = Math.round(eleT.attr("data-height") * widthRatio);
    w = Math.round(eleT.attr("data-width") * widthRatio);
    eleT.css({   
        'background-size': w + 'px ' + h + 'px',
        'top': 'calc(50% + ' + Math.round(90 * widthRatio - h / 2) + 'px)',
        'left': 'calc(50% + ' + Math.round(100 * widthRatio - w / 2) + 'px)',
    });

    eleT =  $('.layer-5');
    h = Math.round(eleT.attr("data-height") * widthRatio);
    w = Math.round(eleT.attr("data-width") * widthRatio);
    eleT.css({
        'background-size': w + 'px ' + h + 'px',
        'top': 'calc(50% - ' + Math.round(h / 2) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2) + 'px)',
    });

    eleT =  $('.layer-6');
    h = Math.round(eleT.attr("data-height") * widthRatio);
    w = Math.round(eleT.attr("data-width") * widthRatio);
    eleT.css({
        'background-size': w + 'px ' + h + 'px',
        'top': 'calc(50% - ' + Math.round(w * .78) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2) + 'px)',
    });

    eleT =  $('.main-button');
    h = Math.round(parseInt(eleT.css("height"),10) * widthRatio);
    w = Math.round(parseInt(eleT.css("width"),10) * widthRatio);
    
    eleT =  $('#media-button');
    eleT.css({
        'width': w + 'px',
        'height': h + 'px',
        'top': 'calc(50% - ' + Math.round(h / 2 - 110 * widthRatio) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2 + 205 * widthRatio) + 'px)',
    });

    eleT =  $('#features-button');
    eleT.css({
        'width': w + 'px',
        'height': h + 'px',
        'top': 'calc(50% - ' + Math.round(h / 2 - 30 * widthRatio) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2 + 240 * widthRatio) + 'px)',
    });

    eleT =  $('#trailer-button');
    eleT.css({
        'width': w + 'px',
        'height': h + 'px',
        'top': 'calc(50% - ' + Math.round(h / 2 + 50 * widthRatio) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2 + 250 * widthRatio) + 'px)',
    });
    console.log(eleT.css("left"));

    eleT =  $('#algorithms-button');
    eleT.css({
        'width': w + 'px',
        'height': h + 'px',
        'top': 'calc(50% - ' + Math.round(h / 2 + 50 * widthRatio) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2 - 250 * widthRatio) + 'px)',
    });
    console.log(eleT.css("left"));
    eleT =  $('#github-button');
    eleT.css({
        'width': w + 'px',
        'height': h + 'px',
        'top': 'calc(50% - ' + Math.round(h / 2 - 30 * widthRatio) + 'px)',
        'left': 'calc(50% - ' + Math.round(w / 2 - 240 * widthRatio) + 'px)',
    });


    // temp = Math.round(high * .266 * .8);
    // console.log("l6  " + temp);
    // $('.layer-6').css({
    //     'background-size': temp + 'px ' + temp / 2 + 'px',
    //     'top': 'calc(50% - ' + Math.round(temp * .78) + 'px)',
    //     'left': 'calc(50% - ' + Math.round(temp / 2) + 'px)',
    // });

    // temp = Math.round(high * .266 * .8);
    // $('.button').css({
    //     'background-size': temp + 'px ' + temp / 4 + 'px',
    //     'top': 'calc(50% - ' + Math.round(temp * .78) + 'px)',
    //     'left': 'calc(46% - ' + Math.round(temp / 2) + 'px)',
    // });

    // console.log("l4  " + temp);
    // $('.layer-4').css({
    //     'background-size': temp + 'px ' + temp + 'px',
    //     'top': 'calc(50% + ' + Math.round(high * .0291 * .8) + 'px)',
    //     'left': 'calc(50% + ' + Math.round(high * .0552 * .8) + 'px)',
    // });

    // temp = Math.round(high * .117 * .8);

}