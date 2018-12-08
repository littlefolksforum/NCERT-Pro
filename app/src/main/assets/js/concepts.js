var $conceptsWrapper;
var SWIG_TEMPLATES = {};

$(function() {
    $conceptsWrapper = $("#concepts-wrapper");
    compileSwigTemplates();
    configureMathjax();
    
    var longPressTimer;
    var isLongPress = false;
    $(document).on("touchstart", '.js-bookmark', function(event) {
        longPressTimer = setTimeout(function() {
            console.log('long press');
            onBookmarkClicked(event);
            isLongPress = true;
        }, 500);
    }).on("touchend touchleave", '.js-bookmark', function(event) {
        if (event.type === 'touchend' && !isLongPress)
            onBookmarkClicked(event);
        isLongPress = false;
        clearTimeout(longPressTimer);
    });

    $(document).on('click', '.js-gif-thumbnail', onGifClick);
    $(document).on('click', '.js-image', onImageClick);
    // displayMockConcepts();
});

function configureMathjax() {
    MathJax.Hub.Config({
        showProcessingMessages: false,
        showMathMenu: false,
        messageStyle: "none",
        tex2jax: {
            inlineMath: [['$$','$$'], ['\\(','\\)']],
            displayMath: [['$$$','$$$'], ['\\(','\\)']]
        },
        styles: {
            ".MathJax_SVG svg > g, .MathJax_SVG_Display svg > g": {
                fill: "#4A4A4A",
                stroke: "#4A4A4A"
            }
        }
    });

    MathJax.Hub.Register.StartupHook('End', function(){
        if ('jsObject' in window)
            jsObject.mathjax_done();
    });
}

function onGifClick(e) {
    var $this = $(this);
    var videoURL = $this.data("url");

    if (videoURL && 'jsObject' in window) {
        jsObject.playGIFVideo(videoURL);
    }
}

function onImageClick(e) {
    var imgUrl = $(this).attr('src');
    if (imgUrl && 'jsObject' in window) {
        jsObject.displayImage(imgUrl);
    }
}

function compileSwigTemplates() {
    var $rawTemplates = $('.swig-tpl');

    if (!$rawTemplates.length)
        return;

    $rawTemplates.each(function() {
        var name = $(this).data('tpl');
        var content = $(this).html();
        SWIG_TEMPLATES[name] = swig.compile(content || '');
    });
}

function renderTemplate(template, context) {
    if (SWIG_TEMPLATES.hasOwnProperty(template)) {
        return SWIG_TEMPLATES[template](context || {});
    }
    return '';
}

function onBookmarkClicked(e) {
    var $bookmarkBtn = $(e.target).closest(".js-bookmark");
    var id = $bookmarkBtn.data("id") || 0;
    var isBookmarked = $bookmarkBtn.data("is-bookmarked") || 0;
    var inProgress = $bookmarkBtn.data("in-progress") || 0;

    if (inProgress) {
        return;
    }

    if (e.type === 'touchstart') {
        showNotebooks(id || 0, isBookmarked);
        return;
    }

    $bookmarkBtn.data("in-progress", 1);
    console.log('bookmark', id);

    if (isBookmarked) {
        $bookmarkBtn.data("is-bookmarked", 0);
        $bookmarkBtn.removeClass("active");
        removeBookmark(id || 0);
    } else {
        $bookmarkBtn.data("is-bookmarked", 1);
        $bookmarkBtn.addClass("active");
        addBookmark(id || 0);
    }
}

function showNotebooks(id, isBookmarked) {
    console.log('addBookmark', id);
    if ("jsObject" in window)
        jsObject.showNotebooks(id, isBookmarked);
}

function addBookmark(id) {
    console.log('addBookmark', id);
    if ("jsObject" in window)
        jsObject.addBookmark(id);
}

function removeBookmark(id) {
    console.log('removeBookmark', id);
    if ("jsObject" in window)
        jsObject.removeBookmark(id);
}

function showBookmarked(id) {
    console.log('showBookmarked', id);
    var $bookmarkBtn = $("#bookmark_"+id);
    
    if ($bookmarkBtn.length) {
        $bookmarkBtn.data("in-progress", 0);
        $bookmarkBtn.data("is-bookmarked", 1);
        $bookmarkBtn.addClass("active");
    }
}

function showNonBookmarked(id) {
    console.log('showNonBookmarked', id);
    var $bookmarkBtn = $("#bookmark_"+id);
    
    if ($bookmarkBtn.length) {
        $bookmarkBtn.data("in-progress", 0);
        $bookmarkBtn.data("is-bookmarked", 0);
        $bookmarkBtn.removeClass("active");
    }
}

function displayConcepts(conceptsJSONEncoded) {
    try {
        var conceptsJSON = window.atob(conceptsJSONEncoded);
        var concepts = JSON.parse(conceptsJSON);
        console.log('concepts', conceptsJSON.toString());
        var conceptsHTML = '';

        concepts.map(function(concept, index){
            // section.items.map(function(concept, index){
                var count = index + 1;
                concept.counter = count > 9 ? count : ('0' + count);
                conceptsHTML += renderTemplate('concept', concept);
            // });
        });

        $conceptsWrapper.html(conceptsHTML);
        setTimeout(function(){
            MathJax.Hub.Queue(['Typeset',MathJax.Hub]);
        },0);
    } catch(e) {
        console.error('Failed to parse concepts JSON');
    }
}

function displayMockConcepts() {
    var concepts = [
        {
           "has_items": true,
           "sequence_no": 1,
           "name": "Points to remember",
           "items": [
             {
                    "is_bookmarked": false,
                    "long_image": "",
                    "long_text": "",
                    "type": "law",
                    "text": "To every action, there is always equal and opposite reaction, i.e.<br>$${ \\overrightarrow {{ F }_{ 12 }} } \\quad =\\quad - \\overrightarrow {{ F }_{ 21 } }$$&nbsp;<br>",
                    "image": "",
                    "sequence_no": 226,
                    "heading": "Third Law of Motion",
                    "id": 10916
                },
                {
                    "is_bookmarked": false,
                    "long_image": "",
                    "long_text": "",
                    "type": "definition",
                    "text": "",
                    "image": "https://haygot.s3.amazonaws.com/cheatsheet/13563.png",
                    "sequence_no": 185,
                    "heading": "FBD for a single object with multiple forces acting on it",
                    "id": 13563
                },
                {
                    "is_bookmarked": false,
                    "long_image": "",
                    "long_text": "",
                    "type": "example",
                    "text": "A constant force ($$F$$) is applied on a stationary&nbsp;particle of mass $$m$$. The velocity attained by the&nbsp;particle in a certain displacement will be proportional to:<br><br>We know,<br>$$F=ma$$<br>$$a=\\dfrac{F}{m}$$<br>Moreover,<br>$$v^2=u^2+2aS$$<br>$$\\Rightarrow v^2=2\\times \\dfrac{F}{m}\\times S$$<br>$$v=\\sqrt{2\\times \\dfrac{F}{m}\\times S}$$<br>$$\\Rightarrow v \\propto \\dfrac{1}{\\sqrt m}$$<br><br>",
                    "image": "",
                    "sequence_no": 226,
                    "heading": "Motion of object under constant force",
                    "id": 14588
                }
            ]
        }
    ];
    displayConcepts(window.btoa(JSON.stringify(concepts)));
}
