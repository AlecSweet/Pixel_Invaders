
$(document).ready(function(){
    var ParallaxManager, ParallaxLayer;
    //var canvas = document.getElementById("canvas");
    positionElements();
    initMainButtons();

    ParallaxLayer = (function() {
        function ParallaxLayer(el) {
            this.el = el;
            this.speed = parseFloat(this.el.getAttribute('data-parallax-speed'));
            this.maxScroll = parseInt(this.el.getAttribute('data-max-scroll'));
            this.curScrollY = 0;
            this.curPanX = 0;
            this.curPanY = 0;
        }

        ParallaxLayer.prototype.scrollTranslate = function(scrollY) {
            this.curScrollY = Math.round(-scrollY * this.speed);
        };

        ParallaxLayer.prototype.panTranslate = function(x, y) {
            this.curPanX = Math.round(x * this.speed * .4);
            this.curPanY = Math.round(y * this.speed * .4);
        };

        ParallaxLayer.prototype.update = function() {
            var temp1 = this.curPanY + this.curScrollY;
            //$(this.el).css('transform', 'translate(' + 0 + 'px, ' + 0 + 'px)' );
            $(this.el).css('transform', 'translate(' + this.curPanX + 'px, ' + temp1 + 'px)' );
        };

        return ParallaxLayer;
    
    })();
    
    ParallaxManager = (function() {
        ParallaxManager.prototype.layers = [];
        
        function ParallaxManager(elements, iframe) {
            this.mouseX = Math.round($(window).width() / 2);
            this.mouseY = Math.round($(window).height() / 2) - window.pageYOffset;
            this.curLocX = 0;
            this.curLocY = 0;
            this.hasMouseListener = true;
            if (Array.isArray(elements) && elements.length) {
                this.elements = elements;
            }
            if (typeof elements === 'object' && elements.item) {
                this.elements = Array.prototype.slice.call(elements);
            } else if (typeof elements === 'string') {
                this.elements = document.querySelectorAll(elements);
                if (this.elements.length === 0) {
                    throw new Error('Parallax: No elements found');
                }
                this.elements = Array.prototype.slice.call(this.elements);
            } else {
                throw new Error('Parallax: Element variable is not a querySelector string, Array, or NodeList');
            }
            for (var i in this.elements) {
                this.layers.push(new ParallaxLayer(this.elements[i]));
            }
                   
            window.setInterval(this.update.bind(this), 16);

            $(document).on('mousemove', function(event) {
                this.mouseX = event.pageX;
                this.mouseY = event.pageY - window.pageYOffset;
            }.bind(this));

            $(document).on('mouseout', function() {
                this.mouseX = Math.round($(window).width() / 2);
                //this.mouseY = Math.round($(window).height() / 2) - window.pageYOffset;
            }.bind(this));

            /*document.getElementById('cover').contentWindow.addEventListener('mousemove', function(event) {
                this.mouseX = event.pageX;
                this.mouseY = event.pageY - window.pageYOffset;
            }.bind(this));*/

            $(document).on('touchstart', function onFirstTouch() {
                console.log('hit');
                $(document).off('touchstart');
                $(document).off('mousemove');
                $(document).off('mouseout');
                //$(document.getElementById('cover').contentWindow).off('mousemove');
                this.mouseX = Math.round($(window).width() / 2);
                this.mouseY = Math.round($(window).height() / 2) - window.pageYOffset;
                for (var i in this.layers) {
                    this.layers[i].panTranslate(this.mouseX, this.mouseY); 
                }
                this.hasMouseListener = false;
            });
            
        }

        ParallaxManager.prototype.triggerMouseMove = function(){
            //document.dispatchEvent(new Event('mousemove'));
            //document.trigger('mousemove');
        }

        ParallaxManager.prototype.update = function() {
            if(this.hasMouseListener) {
                //var panX = this.mouseX - Math.round(document.documentElement.clientWidth / 2);
                //var panY = this.mouseY - Math.round(document.documentElement.clientHeight / 2) - window.pageYOffset;
                //var newX = this.mouseX - Math.round(document.documentElement.clientWidth / 2);
                //var newY = this.mouseY - Math.round(document.documentElement.clientHeight / 2) - window.pageYOffset;
                var difX = this.mouseX - Math.round(document.documentElement.clientWidth / 2) - this.curLocX;
                var difY = this.mouseY - Math.round(document.documentElement.clientHeight / 2) - window.pageYOffset - this.curLocY;
                var dist = Math.sqrt(difX * difX + difY * difY);

                if(dist > 20)
                {
                    this.curLocX += Math.round(20 * difX / dist);
                    this.curLocY += Math.round(20 * difY / dist);
                }else if(dist > 10){
                    this.curLocX += Math.round(.5 * difX);
                    this.curLocY += Math.round(.5 * difY);
                }
                for (var i in this.layers) {
                    this.layers[i].scrollTranslate(window.pageYOffset); 
                    //this.layers[i].panTranslate(-panX, -panY);
                    this.layers[i].panTranslate(-this.curLocX, -this.curLocY);  
                    this.layers[i].update();
                }
            } else {
                for (var i in this.layers) {
                    this.layers[i].scrollTranslate(window.pageYOffset); 
                    this.layers[i].update();
                }
            }
        }

        return ParallaxManager;
    })();

    var paraManager = new ParallaxManager('.parallax-layer', document.getElementById('trailer'));

    function titleAnimationDelay() {
        titleAnimationPlay(1, 1);
    }

    function titleAnimationPlay(animState, direction) {
        $('.title').css({
            'background-image': 'url(img/title' + animState + '2.png)',
        });
        if(animState == 0) {
            window.setTimeout(function(){titleAnimationDelay()}, Math.random() * 5000 + 1000);
        } else if(animState < 4 && direction == 1) {
            window.setTimeout(function(){titleAnimationPlay(++animState, 1)}, 32);
        } else {
            window.setTimeout(function(){titleAnimationPlay(--animState, -1)}, 32);
        }
    }
 
    var timeoutID = window.setTimeout(function(){titleAnimationDelay()}, 1000);
    
    var timeoutID2 = window.setTimeout(function(){toggleButtons(true)}, 1400);


    /*var videoPlayer = new Vimeo.Player('video', {url: 'https://player.vimeo.com/video/300653546?title=0&byline=0&portrait=0'});
    video01Player.on('play', function() {
        console.log('Played the first video');
    });*/
    //var player = new Vimeo.Player(document.getElementById('trailer'));

    /*player.on('play', function() {
        console.log('hitPlay');
        $('.parallax-container').focus();
        $(document.getElementById('trailer')).css({
            'pointer-events': 'none'
        });
        paraManager.triggerMouseMove();
        paraManager.update()
    });*/
});


















