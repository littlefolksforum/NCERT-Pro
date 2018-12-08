var $questionWrapper;
var passageMarkerLines = {};
var SWIG_TEMPLATES = {};
var QSTYLE_ASSERTION = 'assertion';
var QSTYLE_SINGLE_CORRECT = 'single correct';
var QSTYLE_MULTIPLE_CORRECT = 'multiple correct';
var QSTYLE_BLANK = 'blank';
var QSTYLE_MATRIX = 'matrix';
var QSTYLE_TRUE_FALSE = 'true-false';
var QUESTION_UNIT_CONTAINER_SELECTOR = '.js-question-unit-container';
var QUESTION_SELECTOR = '.js-question';
var OPTION_LIST_ITEM_SELECTOR = '.js-option-list-item';
var OPTION_LIST_SELECTED_ITEM_SELECTOR = '.js-option-list-item.selected';
var MATRIX_CHOICE_SELECTOR = '.js-matrix-choice';
var MATRIX_CHOICE_SELECTED_ITEM_SELECTOR = '.js-matrix-choice.selected';
var BLANK_INPUT_SELECTOR = '.js-blank-input';
var SELECTED_CLASS_NAME = 'selected';

$(function() {
    $questionWrapper = $("#content-wrapper");
    compileSwigTemplates();
    configureMathjax();
    initQuestionEventListeners();
    $(document).on('click', '.js-image', onImageClick);
    // displayMockQuestion();
    // displayMockPassageQuestion();
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

function onImageClick(e) {
    var imgUrl = $(this).attr('src');
    if (imgUrl && 'jsObject' in window) {
        jsObject.displayImage(imgUrl);
    }
}

function initQuestionEventListeners() {

    // Choice options list
    $(document).on('click', OPTION_LIST_ITEM_SELECTOR, function(e) {
        var $choice_clicked = $(e.target).closest(OPTION_LIST_ITEM_SELECTOR);
        var $question = $choice_clicked.closest(QUESTION_SELECTOR);
        var question_style = $question.data('style') || '';

        if ($question.data("attempted") == true) {
            return;
        }

        var $selected_choices = $question.find(OPTION_LIST_SELECTED_ITEM_SELECTOR);

        // If choice is already selected
        if ($choice_clicked.hasClass(SELECTED_CLASS_NAME)) {
            $choice_clicked.removeClass(SELECTED_CLASS_NAME);
            onChoiceChange($question.data("qid"), $choice_clicked.data('choice-id'), false);
            return;
        }

        if (question_style != QSTYLE_MULTIPLE_CORRECT) {
            var $selected_choices = $question.find(OPTION_LIST_SELECTED_ITEM_SELECTOR);
            if ($selected_choices.length) {
                $selected_choices.each(function() {
                    $(this).removeClass(SELECTED_CLASS_NAME);
                });
            }
        }

        $choice_clicked.addClass(SELECTED_CLASS_NAME);
        onChoiceChange($question.data("qid"), $choice_clicked.data('choice-id'), true);
    });

    // Matrix options
    $(document).on('click', MATRIX_CHOICE_SELECTOR, function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $choice_clicked = $(e.target).closest(MATRIX_CHOICE_SELECTOR);
        var $question = $choice_clicked.closest(QUESTION_SELECTOR);

        if ($question.data("attempted") == true){
            return; // Already attempted
        }

        var choice_value = parseInt($choice_clicked.data('value'), 10);

        // If choice is already selected
        if ($choice_clicked.hasClass(SELECTED_CLASS_NAME)) {
            $choice_clicked.removeClass(SELECTED_CLASS_NAME);
            onMatrixChoiceChange($question.data("qid"), choice_value, false);
            return;
        }

        $choice_clicked.addClass(SELECTED_CLASS_NAME);
        onMatrixChoiceChange($question.data("qid"), choice_value, true);
    });

    // Blank
    $(document).on('keyup', BLANK_INPUT_SELECTOR, function(e) {
        var $input = $(e.target).closest(BLANK_INPUT_SELECTOR);
        var $question = $input.closest(QUESTION_SELECTOR);
        onInputValueChange($question.data("qid"), $input.val())
    });

    // Bookmarks
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

    $(document).on('click', '.js-ask-doubt', function(e) {
        var $askDoubtBtn = $(e.target).closest('.js-ask-doubt');
        var questionId = $askDoubtBtn.data("id") || 0;
        console.log('askDoubt', questionId);
        if ("jsObject" in window) {
            jsObject.askDoubt(questionId);
        }
    });

    $(document).on('click', '.js-concept-btn', function(e) {
        var $question = $(e.target).closest(QUESTION_SELECTOR);
        var questionId = $question.data("qid") || 0;
        console.log('showConcepts', questionId);
        if ("jsObject" in window) {
            jsObject.showConcepts(questionId);
        }
    });

    $(document).on('click', '.js-lecture-btn', function(e) {
        var $question = $(e.target).closest(QUESTION_SELECTOR);
        var questionId = $question.data("qid") || 0;
        console.log('showLectures', questionId);
        if ("jsObject" in window) {
            jsObject.showLectures(questionId);
        }
    });

    $(document).on('click', '.js-toggle-passage-height-btn', function(e) {
        console.log('height');
        var $passageText = $(this).closest('.js-passage').find('.js-passage-text');

        if ($passageText.hasClass('collapsed')) {
            $passageText.removeClass('collapsed');
            $(this).text('Read less');
        } else {
            $passageText.addClass('collapsed');
            $(this).text('Read more');
        }
    });
}

function initPassage() {
    var $passageText = $(document).find(".js-passage-text");
    if ($passageText.length) {
        var $passageTextLineNumWrapper = $passageText.find(".line-number-wrapper");
        var passageHeight = $passageText.height();
        var lineHeight = parseInt($passageText.css('line-height'), 10);
        var totalLines = passageHeight / lineHeight;

        passageMarkerLines = {};

        for (var lineNumber = 1; lineNumber <= totalLines; lineNumber++) {
            if (lineNumber == 1 || lineNumber % 5 == 0) {
                $passageTextLineNumWrapper.append($("<span>", { class: "line-number" }).text(lineNumber).css("top", (lineNumber-1)*lineHeight));
            }
        }

        var passageTopOffset = $passageText.offset().top;
        var $passageMarkers = $passageText.find('.x-marker');
        $passageMarkers.each(function(){
            var $marker = $(this);
            var markerID = $marker.text();
            $marker.data("marker", markerID);
            var topOffset = $marker.offset().top;
            var lineNum = Math.ceil((topOffset - passageTopOffset) / lineHeight);
            passageMarkerLines['ref-' + markerID] = lineNum;
            $marker.text('');
        });
        console.log('passageMarkerLines', passageMarkerLines);

        $passageText.addClass('collapsed');
    }
}

function initPassageQuestion() {
    var $questions = $(QUESTION_UNIT_CONTAINER_SELECTOR).find(".js-question");
    $questions.each(function(index) {
        var $question = $(this);
        var $refs = $question.find('.x-ref');
        $refs.each(function() {
            var $ref = $(this);
            var referencedMarker = $ref.text();
            var referencedMarkerKey = 'ref-' + referencedMarker;

            $ref.data("marker", referencedMarker);
            if (passageMarkerLines.hasOwnProperty(referencedMarkerKey)) {
                $ref.text(passageMarkerLines[referencedMarkerKey]).css({color: 'inherit'});
            }
        });

        if (index != 0) {
            $question.hide();
        }
    });
}

function onInputValueChange(questionID, value) {
    console.log('onInputValueChange', questionID, value);
    if ('jsObject' in window)
        jsObject.onInputValueChange(questionID, value);
}

function onMatrixChoiceChange(questionID, choiceValue, isSelected) {
    console.log('onMatrixChoiceChange', questionID, choiceValue, isSelected);
    if ('jsObject' in window)
        jsObject.onMatrixChoiceChange(questionID, choiceValue, isSelected);
}

function onChoiceChange(questionID, choiceValue, isSelected) {
    console.log('onChoiceChange', questionID, choiceValue, isSelected);
    if ('jsObject' in window)
        jsObject.onChoiceChange(questionID, choiceValue, isSelected);
}

function getStatsHTML(questionInfoJSONEncoded, is_out_of_syllabus) {
    var html = '';
    try {
        var questionInfoJSON = window.atob(questionInfoJSONEncoded);
        var questionInfo = JSON.parse(questionInfoJSON);
        questionInfo.is_out_of_syllabus = is_out_of_syllabus === "true" ? true : false;
        console.log('onQuestionAttempted response', questionInfo);

        html = renderTemplate('question_stats', questionInfo);
        
    } catch(e) {
        console.error('Failed to parse question info JSON');
    }
    return html;
}

function onQuestionAttempted(responseJSONEncoded, questionInfoJSONEncoded, is_out_of_syllabus) {
    var $question = $(document).find(QUESTION_SELECTOR);
    $question.data("attempted", true);

    var style = $question.data("style") || "";
    var $solution = $question.find(".js-solution");
    var $askDoubtBtn = $(".js-ask-doubt");
    var response = decodedBase64ToJSONObject(responseJSONEncoded);

    console.log('onQuestionAttempted response', response);
    var rightAnswers = response.right_answers || [];
    console.log('rightAnswers', rightAnswers);
    
    if (style == QSTYLE_SINGLE_CORRECT || style == QSTYLE_MULTIPLE_CORRECT || style == QSTYLE_ASSERTION || style == QSTYLE_TRUE_FALSE) {
        displayQuestionAnswer_optionList($question, rightAnswers);
    }
    else if (style == QSTYLE_MATRIX) {
        displayQuestionAnswer_matrix($question, rightAnswers);
    }
    else if (style == QSTYLE_BLANK) {
        displayQuestionAnswer_blank($question, response.is_correctly_answered, response.right_answer || "");
    }

    $solution.find(".js-stats").html(getStatsHTML(questionInfoJSONEncoded, is_out_of_syllabus));
    $solution.show();    
    $askDoubtBtn.show();
}

function displayQuestionAnswer_optionList($question, rightAnswers) {
    $question.find(OPTION_LIST_ITEM_SELECTOR).each(function() {
        $choice = $(this);
        var choiceId = parseInt($choice.data("choice-id"), 10);
        var isRight = $.inArray(choiceId, rightAnswers) > -1;
        var isSelected = $choice.hasClass(SELECTED_CLASS_NAME);

        if (isSelected) {
            if (isRight) {
                $choice.addClass("correct");
            } else {
                $choice.addClass("wrong");
            }
            
            $choice.find(".js-choice-result").html("<div class='text'>Your answer</div>");
        } else if (isRight) {
            $choice.addClass("correct");
            $choice.find(".js-choice-result").html("<div class='text'>You missed</div>");
        }
    });
}

function displayQuestionAnswer_matrix($question, rightAnswers) {
    var $selectedChoices = $question.find(MATRIX_CHOICE_SELECTED_ITEM_SELECTOR);
    $selectedChoices.addClass("wrong");

    $.each(rightAnswers, function(index, value) {
        var $choice = $question.find(".js-matrix-choice-"+ value);
        $choice.removeClass("wrong").addClass("correct");
    });
}

function displayQuestionAnswer_blank($question, isCorrectlyAnswered, rightAnswer) {
    var $inputWrapper = $question.find(".js-input-wrapper");
    var $input = $question.find(".js-blank-input");
    $input.prop("disabled", true);
    $input.attr("placeholder", "");

    var user_answer = $input.val().trim();

    if (!isCorrectlyAnswered) {
        $inputWrapper.addClass("wrong");
        $inputWrapper.find(".choice-result").html("Wrong answer");
        $inputWrapper.find(".js-correct-answer").html(rightAnswer);
    } else {
        $inputWrapper.addClass("correct");
        $inputWrapper.find(".choice-result").html("Correct answer");
    }
}

function onBookmarkClicked(e) {
    var $bookmarkBtn = $(e.target).closest('.js-bookmark');
    var questionId = $bookmarkBtn.data("id") || 0;
    var isBookmarked = $bookmarkBtn.data("is-bookmarked") || 0;
    var inProgress = $bookmarkBtn.data("in-progress") || 0;

    if (inProgress) {
        return;
    }

    if (e.type === 'touchstart') {
        showNotebooks(questionId || 0, isBookmarked);
        return;
    }

    $bookmarkBtn.data("in-progress", 1);

    if (isBookmarked) {
        $bookmarkBtn.data("is-bookmarked", 0);
        $bookmarkBtn.removeClass("active");
        removeBookmark(questionId);
    } else {
        $bookmarkBtn.data("is-bookmarked", 1);
        $bookmarkBtn.addClass("active");
        addBookmark(questionId);
    }
}

function addBookmark(questionId) {
    console.log('addBookmark', questionId);
    if ("jsObject" in window)
        jsObject.addBookmark(questionId);
}

function removeBookmark(questionId) {
    console.log('removeBookmark', questionId);
    if ("jsObject" in window)
        jsObject.removeBookmark(questionId);
}

function showNotebooks(questionId, isBookmarked) {
    console.log('addBookmark', questionId);
    if ("jsObject" in window)
        jsObject.showNotebooks(questionId, isBookmarked);
}

function displayBookmarked(questionId) {
    console.log('displayBookmarked', questionId);
    var $bookmarkBtn = $("#bookmark_" + questionId);

    if ($bookmarkBtn.length) {
        $bookmarkBtn.data("in-progress", 0);
        $bookmarkBtn.data("is-bookmarked", 1);
        $bookmarkBtn.addClass("active");
    }
}

function displayNonBookmarked(questionId) {
    console.log('displayNonBookmarked', questionId);
    var $bookmarkBtn = $("#bookmark_" + questionId);

    if ($bookmarkBtn.length) {
        $bookmarkBtn.data("in-progress", 0);
        $bookmarkBtn.data("is-bookmarked", 0);
        $bookmarkBtn.removeClass("active");
    }
}

function displayPassage(passageJSONEncoded) {
    try {
        var passageQuestion = decodedBase64ToJSONObject(passageJSONEncoded);
        console.log('passageQuestion', passageQuestion);

        passageQuestion.testType = 'assessment';
        $questionWrapper.html(renderTemplate('question_wrapper', passageQuestion));
        $questionWrapper.find('.js-passage-container').html(renderTemplate('passage', passageQuestion));

        if (passageQuestion.passage) {
            initPassage();
        } else {
            $questionWrapper.css('padding-top', 0);
        }

        setTimeout(function(){
            MathJax.Hub.Queue(['Typeset',MathJax.Hub]);
        },0);
    } catch(e) {
        console.error('Failed to parse question JSON');
    }
}

function displayQuestion(questionJSONEncoded) {
    try {
        var question = decodedBase64ToJSONObject(questionJSONEncoded);

        console.log('question'. question);

        question.testType = 'assessment';
        question.selected_choices = question.selected_choices || [];

        if (question.question_linked_to_id) {
            question.parentQuestionStyle = 'passage';
        } else {
            $questionWrapper.html(renderTemplate('question_wrapper', question));
        }

        $questionWrapper.find(QUESTION_UNIT_CONTAINER_SELECTOR).html(renderTemplate('question_unit', question));

        if (question.question_linked_to_id) {
            initPassageQuestion();
        }

        setTimeout(function(){
            MathJax.Hub.Queue(['Typeset',MathJax.Hub]);
        },0);
    } catch(e) {
        console.error('Failed to parse question JSON');
    }
}

function decodedBase64ToJSONObject(base64EncodedString) {
    try {
        var jsonString = window.atob(base64EncodedString);
        var jsonObject = $.parseJSON(jsonString);

        return jsonObject;
    } catch(e) {
        console.log('base64EncodedString', base64EncodedString);
        console.error('Error while decoding to json object', e);
    }
}

function displayMockPassageQuestion() {
    var passage = {
        "passage_image": "",
        "question_image": "",
        "hint_available": false,
        "passage_footer": "",
        "question_linked_to_id": null,
        "sequence_no": 13,
        "assertion": "",
        "hint": "",
        "can_ask_doubt": false,
        "is_bookmarked": false,
        "passage": "Neera : Hi, Amina! __(1)__ <br/>Amina : Well, __(2)__ I suppose. I mean, the starting time's great. <br/><br/><div>Neera : Why's that ?&#160;<br/><span>Amina : It doesn't start till noon, so I don't have to get up early, and I needn't set my alarm and all that.&#160;<br/><br/></span></div><div><span>Neera : Cool! But then, you have to work late, right ?<br/>Amina : Yeah, __(3)__.&#160;Also, we aren't allowed to leave till the last customer's gone, and sometimes that's around midnight! <br/><br/></span></div><div><span>Neera : __(4)__.<br/>Amina : And we are supposed to speak really carefully to customers and say things like &#34;Have a nice day now&#34;.&#160;<br/><br/></span></div><div><span>Neera : Yuck!<br/>Amina : And we have to wear stupid hats!<br/><br/></span></div><div><span>Neera : __(5)__.&#160;<br/>Amina : The worst thing is you don't get a moment's rest to sit down or anything.&#160;<br/><br/></span></div><div><span>Neera : Mmmmm... __(6)__<br/>Amina : No, hopefully I'll find something better, and I'll move on.</span></div>",
        "solution_links": [],
        "show_passage_on_left": true,
        "already_attempted": false,
        "correctly_answered": false,
        "passage_header": "Read the following conversation and answer the given questions:",
        "solution_image": "",
        "reason": "",
        "hint_image": "",
        "multiple_correct": false,
        "question_style": "passage",
        "solution_available": false,
        "solution": "",
        "choices": [],
        "question_linked": false,
        "goal_id": 713008,
        "question_id": 313866
    };

    var passageQuestion = {
        "passage_image": "",
        "question_image": "",
        "hint_available": false,
        "passage_footer": "",
        "question_linked_to_id": 313866,
        "sequence_no": 13,
        "assertion": "",
        "hint": "",
        "can_ask_doubt": false,
        "question": "<span>Fill in blank no (4) with the most suitable phrase from the options given below.</span>",
        "is_bookmarked": false,
        "passage": "",
        "solution_rating": 0,
        "solution_links": [],
        "already_attempted": false,
        "solution_id": 545816,
        "correctly_answered": false,
        "passage_header": "",
        "solution_image": "",
        "reason": "",
        "hint_image": "",
        "multiple_correct": false,
        "question_style": "single correct",
        "solution_available": true,
        "solution": "Option A) and Option C) are positive adjectives.<div>Option B) used terrifying but the situation according to the conversation is not terrifying.</div><div>Awful means unpleasant in this context in the passage.</div><div>So, the correct answer is D) Oh, no, that's awful.</div>",
        "choices": [
            {
                "is_right": false,
                "choice_id": 1064032,
                "choice": "Oh, that's really great",
                "image": "",
                "label": "A"
            },
            {
                "is_right": false,
                "choice_id": 1064033,
                "choice": "No, that's terrifying",
                "image": "",
                "label": "B"
            },
            {
                "is_right": false,
                "choice_id": 1064034,
                "choice": "How sweet that sounds",
                "image": "",
                "label": "C"
            },
            {
                "is_right": true,
                "choice_id": 1064035,
                "choice": "Oh, no, that's awful",
                "image": "",
                "label": "D"
            }
        ],
        "question_linked": true,
        "goal_id": 713008,
        "question_id": 313877
    }

    displayPassage(window.btoa(JSON.stringify(passage)));
    displayQuestion(window.btoa(JSON.stringify(passageQuestion)));
}

function displayMockPassageQuestionNext() {
    var passageQuestion = {
        "passage_image": "",
        "question_image": "",
        "hint_available": false,
        "passage_footer": "",
        "question_linked_to_id" : 313866,
        // "question_linked_to_id": null,
        "sequence_no": 14,
        "assertion": "",
        "hint": "",
        "can_ask_doubt": false,
        "question": "The three richest men in America A.&nbsp;<u>have</u>&nbsp;assets worth more B.&nbsp;<u>than</u>&nbsp;the&nbsp;C.&nbsp;<u>combined assets</u>&nbsp;of the sixty poorest countries&nbsp;D.&nbsp;<u>of</u>&nbsp;the world<br>",
        "is_bookmarked": false,
        "passage": "",
        "solution_links": [],
        "already_attempted": false,
        "correctly_answered": false,
        "passage_header": "",
        "solution_image": "",
        "reason": "",
        "hint_image": "",
        "multiple_correct": false,
        "question_style": "single correct",
        "solution_available": false,
        "solution": "",
        "choices": [
            {
                "is_right": false,
                "choice_id": 1837578,
                "choice": "A",
                "image": "",
                "label": "A"
            },
            {
                "is_right": false,
                "choice_id": 1837579,
                "choice": "B",
                "image": "",
                "label": "B"
            },
            {
                "is_right": false,
                "choice_id": 1837580,
                "choice": "C",
                "image": "",
                "label": "C"
            },
            {
                "is_right": false,
                "choice_id": 1837581,
                "choice": "D",
                "image": "",
                "label": "D"
            },
            {
                "is_right": true,
                "choice_id": 1837582,
                "choice": "No error",
                "image": "",
                "label": "E"
            }
        ],
        "question_linked": false,
        "goal_id": 713009,
        "question_id": 519040
    }

    displayQuestion(window.btoa(JSON.stringify(passageQuestion)));
}

function displayMockQuestion() {
    var question = {
        id: 43059,
        section_question_id: 2152617,
        sequence_no: 1,
        // question_style: 'blank',
        // question_style: 'matrix',
        question_style: 'single correct',
        // question_style: 'multiple correct',
        question_style: 'true-false',
        // passage: 'A uniform rod of mass 200 gm and length 1 m is initially at rest in a vertical position. The rod is hinged at the centre such that it can rotate freely without friction about a fixed horizontal axis passing through its centre.',
        // passage_image: 'https://haygot.s3.amazonaws.com/questions/44226.jpg',
        question: 'A body slides over an inclined plane inclined at 45$$^{0}$$ to the horizontal. The relationship between the distance<b> </b>travelled<b> s</b> and time <b>t</b> is given by the equation $$s = ct^{2}$$, where $$c = 1.73 m/s^{2}$$. The coefficient of friction between the body and the plane is&nbsp;$$\mu$$. The acceleration down the inclined plane is <b>a</b>. Then',
        image: 'https://haygot.s3.amazonaws.com/questions/44226.jpg',
        assertion: '',
        reason: '',
        choices: [
            {
                id: 167069,
                label: 'A',
                choice: '$$\mu$$ = 0.5'
            },
            {
                id: 167070,
                label: 'B',
                choice: '$$\mu$$ = 0.4'
            },
            {
                id: 167071,
                label: 'C',
                choice: 'a = 2$$\sqrt{3}$$ m/s$$^{2}$$'
            },
            {
                id: 167072,
                label: 'D',
                choice: 'a = $$\sqrt{3}$$ m/s$$^{2}$$'
            },
        ],
        selected_choices: [167070],
         // selected_choices: ['5'],
       // selected_choices: [2, 5, 9, 15],
        mx_l1: [
            "some text here",
            "",
            "Equilibrium in x-direction<br/><br/>(system consist of a uniformly charged<br/>fixed non-conducting spherical shell and neglect effect of induction)",
            "",
            "(A uniformly charged fixed ring)<br><br>Equilibrium of q charge in given direction."
        ],
        mx_l2: [
            "unstable",
            "accelerating (non-equilibrium)",
            "stable",
            "neutral"
        ],
        mx_l1_images: [
            "", //"https://haygot.s3.amazonaws.com/qs_mx/73268_1780.jpg",
            "https://haygot.s3.amazonaws.com/qs_mx/73268_1781.jpg",
            "https://haygot.s3.amazonaws.com/qs_mx/73268_1782.jpg",
            "https://haygot.s3.amazonaws.com/qs_mx/73268_1783.jpg",
            "https://haygot.s3.amazonaws.com/qs_mx/73268_1784.jpg"
        ],
        mx_l2_images: [
            "",
            "",
            "",
            ""
        ],
        "solution_available": true,
        "solution_image": "https://haygot.s3.amazonaws.com/qs_mx/73268_1781.jpg",
        "solution": "<span>Temperature is one of the basic (fundamental) physical quantities.</span>",
    };

    displayQuestion(window.btoa(JSON.stringify(question)));
}
