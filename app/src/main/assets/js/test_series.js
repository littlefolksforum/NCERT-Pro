var $questionWrapper;
var SWIG_TEMPLATES = {};
var QSTYLE_ASSERTION = 'assertion';
var QSTYLE_SINGLE_CORRECT = 'single correct';
var QSTYLE_MULTIPLE_CORRECT = 'multiple correct';
var QSTYLE_BLANK = 'blank';
var QSTYLE_MATRIX = 'matrix';
var QSTYLE_TRUE_FALSE = 'true-false';
var QUESTION_SELECTOR = '.js-question';
var OPTION_LIST_ITEM_SELECTOR = '.js-option-list-item';
var OPTION_LIST_SELECTED_ITEM_SELECTOR = '.js-option-list-item.selected';
var MATRIX_CHOICE_SELECTOR = '.js-matrix-choice';
var MATRIX_CHOICE_SELECTED_ITEM_SELECTOR = '.js-matrix-choice.selected';
var BLANK_INPUT_SELECTOR = '.js-blank-input';
var SELECTED_CLASS_NAME = 'selected';
var REVIEW_BTN_SELECTOR = '.js-mark-for-review';

$(function() {
    $questionWrapper = $("#content-wrapper");
    compileSwigTemplates();
    configureMathjax();
    initQuestionEventListeners();
    $(document).on('click', '.js-image', onImageClick);
    // displayMockQuestion();
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
                    onChoiceChange($question.data("qid"), $(this).data('choice-id'), false);
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

    // Mark for review
    $(document).on('click', REVIEW_BTN_SELECTOR, function(e) {
        var $reviewBtn = $(e.target).closest(REVIEW_BTN_SELECTOR);
        var questionId = $reviewBtn.data('id') || 0;
        var isMarked = $reviewBtn.data('is-marked') ? true : false;
        var inProgress = $reviewBtn.data("in-progress") || 0;

       if (inProgress) {
            return;
        }

       $reviewBtn.data("in-progress", 1);

       if (isMarked) {
            $reviewBtn.data("is-marked", 0);
            $reviewBtn.removeClass("active");
        } else {
            $reviewBtn.data("is-marked", 1);
            $reviewBtn.addClass("active");
        }
        markForReview(questionId, isMarked);
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

function markForReview(questionID, isMarked) {
    console.log('markForReview', questionID, isMarked);
    if ('jsObject' in window)
        jsObject.markForReview(questionID, isMarked);
}

function markedForReview(questionID, isMarked) {
    var $reviewBtn = $("#markForReview_" + questionID);

   if ($reviewBtn.length) {
        $reviewBtn.data("in-progress", 0);

       if (isMarked) {
            $reviewBtn.data("is-marked", 1);
            $reviewBtn.addClass("active");
        } else {
            $reviewBtn.data("is-marked", 0);
            $reviewBtn.removeClass("active");
        }
    }
}

function hideReviewLater(questionID){
    var $reviewBtn = $("#markForReview_" + questionID);
    $reviewBtn.hide();
}

function displayQuestion(questionJSONEncoded) {
    try {
        var questionJSON = window.atob(questionJSONEncoded);
        var question = JSON.parse(questionJSON);
        console.log(question);
        question.selected_choices = question.selected_choices || [];
        question.testType = 'testSeries';

        $questionWrapper.html(renderTemplate('question_wrapper', question));
        $questionWrapper.find('.js-question-unit-container').html(renderTemplate('question_unit', question));
        setTimeout(function(){
            MathJax.Hub.Queue(['Typeset',MathJax.Hub]);
        },0);
    } catch(e) {
        console.error('Failed to parse question JSON', e);
    }
}

function displayMockQuestion() {
    var question = {
        id: 43059,
        section_question_id: 2152617,
        sequence_no: 1,
        // question_style: 'blank',
        // question_style: 'matrix',
        question_style: 'single correct',
        question_style: 'true-false',
        // question_style: 'multiple correct',
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
        ]
    };

    displayQuestion(window.btoa(JSON.stringify(question)));
}
