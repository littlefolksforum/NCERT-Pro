var $contentWrapper;
var SWIG_TEMPLATES = {};
var QSTYLE_ASSERTION = 'assertion';
var QSTYLE_SINGLE_CORRECT = 'single correct';
var QSTYLE_MULTIPLE_CORRECT = 'multiple correct';
var QSTYLE_BLANK = 'blank';
var QSTYLE_MATRIX = 'matrix';
var QSTYLE_TRUE_FALSE = 'true-false';
var QSTYLE_SUBJECTIVE = 'subjective';
var QUESTION_UNIT_CONTAINER_SELECTOR = '.js-question-unit-container';
var QUESTION_SELECTOR = '.js-question';
var OPTION_LIST_ITEM_SELECTOR = '.js-option-list-item';
var OPTION_LIST_SELECTED_ITEM_SELECTOR = '.js-option-list-item.selected';
var MATRIX_CHOICE_SELECTOR = '.js-matrix-choice';
var MATRIX_CHOICE_SELECTED_ITEM_SELECTOR = '.js-matrix-choice.selected';
var BLANK_INPUT_SELECTOR = '.js-blank-input';
var SELECTED_CLASS_NAME = 'selected';
var SUBMIT_BTN_SELECTOR = '.js-submit-btn';
var SOLUTION_SELECTOR = '.js-solution';
var SHOW_SOLUTION_BTN_SELECTOR = '.js-show-solution-btn';
var CHOICE_RESULT_SELECTOR = '.js-choice-result';

var questions_global;

$(function() {
    $contentWrapper = $("#content-wrapper");
    compileSwigTemplates();
    configureMathjax();
    initQuestionEventListeners();
    $(document).on('click', '.js-image', onImageClick);
    // displayMockQuestions();
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

        var selected_choices_count = getSelectedOptionsCount($question) || 0;
        var $submitBtn = $question.find(SUBMIT_BTN_SELECTOR);

        // If choice is already selected
        if ($choice_clicked.hasClass(SELECTED_CLASS_NAME)) {
            
            if (selected_choices_count <= 1) {
                $submitBtn.hide();
            }

            $choice_clicked.removeClass(SELECTED_CLASS_NAME);
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
        $submitBtn.show();
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

        var $submitBtn = $question.find(SUBMIT_BTN_SELECTOR);
        var choice_value = parseInt($choice_clicked.data('value'), 10);
        var selected_choices_count = $question.find(MATRIX_CHOICE_SELECTED_ITEM_SELECTOR).length || 0;

        // If choice is already selected
        if ($choice_clicked.hasClass(SELECTED_CLASS_NAME)) {
            if (selected_choices_count <= 1) {
                $submitBtn.hide();
            }
            $choice_clicked.removeClass(SELECTED_CLASS_NAME);
            return;
        }

        $choice_clicked.addClass(SELECTED_CLASS_NAME);
        $submitBtn.show();
    });

    // Blank
    $(document).on('keyup', BLANK_INPUT_SELECTOR, function(e) {
        var $input = $(e.target).closest(BLANK_INPUT_SELECTOR);
        var $question = $input.closest(QUESTION_SELECTOR);
        var $submitBtn = $question.find(SUBMIT_BTN_SELECTOR);

        if ($input.val()) {
            $submitBtn.show();
        } else {
            $submitBtn.hide();
        }
    });

    $(document).on('click', SUBMIT_BTN_SELECTOR, function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $submitBtn = $(e.target).closest(SUBMIT_BTN_SELECTOR);

        if ($submitBtn.hasClass("disabled")) {
            return;
        }

        var $question = $(this).closest(QUESTION_SELECTOR);
        var style = $question.data("style") || '';

        if (style ==  QSTYLE_ASSERTION || style == QSTYLE_SINGLE_CORRECT || style == QSTYLE_MULTIPLE_CORRECT || style == QSTYLE_TRUE_FALSE) {
            submitChoices($question);
        }

        if (style == QSTYLE_BLANK) {
            submitInput($question);
        }

        if (style == QSTYLE_MATRIX) {
            submitMatrix($question);
        }

        $submitBtn.hide();
    });

    $(document).on('click', SHOW_SOLUTION_BTN_SELECTOR, function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $showSolutionBtn = $(e.target).closest(SHOW_SOLUTION_BTN_SELECTOR);
        var $question = $(this).closest(QUESTION_SELECTOR);
        var $questionWrapper = $(this).closest('.js-question-wrapper');
        var style = $question.data("style") || '';

        if (style == QSTYLE_SUBJECTIVE) {
            $showSolutionBtn.hide();
            $question.find(SOLUTION_SELECTOR).show();
            $questionWrapper.find('.js-ask-doubt').show();
        }
    });

    // Bookmarks
    /**var longPressTimer;
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
    });**/

    $(document).on('click', '.js-ask-doubt', function(e) {
        var $askDoubtBtn = $(e.target).closest('.js-ask-doubt');
        var questionId = $askDoubtBtn.data("id") || 0;
        console.log('askDoubt', questionId);
        if ("jsObject" in window && questionId) {
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
        var $passageText = $(this).closest('.js-passage').find('.js-passage-text');

        if ($passageText.hasClass('collapsed')) {
            $passageText.removeClass('collapsed');
            $(this).text('Read less');
        } else {
            $passageText.addClass('collapsed');
            $(this).text('Read more');
        }
    });

    $(document).on('click', '.js-passage-btn-prev', function(e) {
        var $this = $(this);

        console.log('js-passage-btn-prev');

        if ($this.hasClass('disabled')) {
            return;
        }

        var $contentWrapper = $this.closest('.js-question-wrapper');
        var $passageQuesSwitcher = $this.closest('.js-passage-ques-switcher');
        var currentQuesSequence = parseInt($passageQuesSwitcher.data('current-ques-seq'), 10);
        var totalQuesCount = parseInt($passageQuesSwitcher.data('total-ques-count'), 10);
        var newCurrentQuesSequence = currentQuesSequence - 1;
        var $quesUnitConatiner = $contentWrapper.find(QUESTION_UNIT_CONTAINER_SELECTOR);
        var $prevQues = $quesUnitConatiner.find(QUESTION_SELECTOR+"[data-seq-no='"+newCurrentQuesSequence+"']");

        if ($prevQues.length) {
            $quesUnitConatiner.find(QUESTION_SELECTOR).hide();
            $prevQues.show();
            $passageQuesSwitcher.data('current-ques-seq', newCurrentQuesSequence);

            if (newCurrentQuesSequence <= 1) {
                $this.addClass('disabled');
            }

            $passageQuesSwitcher.find('.js-passage-btn-next').removeClass('disabled');
            $passageQuesSwitcher.find('.js-passage-current-ques-seq').text(newCurrentQuesSequence > 9 ? newCurrentQuesSequence : '0'+newCurrentQuesSequence);
        }
    });

    $(document).on('click', '.js-passage-btn-next', function(e) {
        var $this = $(this);
        console.log('js-passage-btn-next');

        if ($this.hasClass('disabled')) {
            return;
        }

        var $contentWrapper = $this.closest('.js-question-wrapper');
        var $passageQuesSwitcher = $this.closest('.js-passage-ques-switcher');
        var currentQuesSequence = parseInt($passageQuesSwitcher.data('current-ques-seq'), 10);
        var totalQuesCount = parseInt($passageQuesSwitcher.data('total-ques-count'), 10);
        var newCurrentQuesSequence = currentQuesSequence + 1;
        var $quesUnitConatiner = $contentWrapper.find(QUESTION_UNIT_CONTAINER_SELECTOR);
        var $nextQues = $quesUnitConatiner.find(QUESTION_SELECTOR+"[data-seq-no='"+newCurrentQuesSequence+"']");

        if ($nextQues.length) {
            var $allQuestions = $quesUnitConatiner.find(QUESTION_SELECTOR).hide();
            $nextQues.show();
            $passageQuesSwitcher.data('current-ques-seq', newCurrentQuesSequence);

            if (newCurrentQuesSequence >= totalQuesCount) {
                $this.addClass('disabled');
            }

            $passageQuesSwitcher.find('.js-passage-btn-prev').removeClass('disabled');
            $passageQuesSwitcher.find('.js-passage-current-ques-seq').text(newCurrentQuesSequence > 9 ? newCurrentQuesSequence : '0'+newCurrentQuesSequence);
        }
    });
}

function getSelectedOptionsCount($question) {
    var $selected_choices = $question.find(OPTION_LIST_SELECTED_ITEM_SELECTOR);
    return $selected_choices.length;
}

function submitChoices($question) {
    var questionId = $question.data("qid") || 0;
    var $selected_choices = $question.find(OPTION_LIST_SELECTED_ITEM_SELECTOR);

    if (!$selected_choices.length) {
        return;
    }

    var choices = [];

    $selected_choices.each(function() {
        choices.push($(this).data('choice-id'));
    });
    submitAnswer(questionId, choices);
}

function submitInput($question) {
    var questionId = $question.data("qid") || 0;
    var choices = $question.find('input').val();
    //submitAnswer(questionId, choices);

    // Fire onAnswerSubmitted from here
    var correct_answer_blank;
    var isresponse_true = false;
    for (var i  = questions_global.length - 1; i >= 0; i--) {
        if(questions_global[i].id == questionId){
            correct_answer_blank = questions_global[i].choices[0].choice;
        }
    }

    if(correct_answer_blank == choices){
        isresponse_true = true;
    }
    console.log('correct_answer_blank', correct_answer_blank);
    var response =  {"is_correctly_answered" : isresponse_true , "right_answer" :correct_answer_blank};
    console.log(response);

    onAnswerSubmitted(questionId,  window.btoa(JSON.stringify(response)));

}

function submitMatrix($question) {
    var questionId = $question.data("qid") || 0;
    var $selected_choices = $question.find(MATRIX_CHOICE_SELECTED_ITEM_SELECTOR);

    if (!$selected_choices.length) {
        return;
    }

    var choices = [];

    $selected_choices.each(function() {
        choices.push($(this).data('value'));
    });
    submitAnswer(questionId, choices);
}

function submitAnswer(questionId, choices) {
    if ('jsObject' in window)
        if(Array.isArray(choices)) {
            console.log('submitAnswer', questionId, choices.join(","));
            //jsObject.submitAnswer(questionId, choices.join(","));
        } else {
            //jsObject.submitAnswer(questionId, choices);
        }
        var isresponse_true = true;
        var correct_answer = [];
        var question_type;
        var correct_answer_value_matrix;
        var correct_answer_true_false;
        var inp_choice = [];
        for (var i = questions_global.length - 1; i >= 0; i--) {
            if(questions_global[i].id == questionId){
                if(questions_global[i].question_style == "single correct" ||
                    questions_global[i].question_style == "true-false" || 
                    questions_global[i].question_style == "multiple correct" || 
                    questions_global[i].question_style == "assertion" || 
                    questions_global[i].question_style == "matrix"){
                    question_type = questions_global[i].question_style;
                    for (var j = questions_global[i].choices.length - 1; j >= 0; j--) {
                        if(questions_global[i].choices[j].is_right == "true"){
                            correct_answer.push(questions_global[i].choices[j].id);
                            if(questions_global[i].question_style == "matrix"){
                                correct_answer_value_matrix = questions_global[i].choices[j].choice;
                            } else if(questions_global[i].question_style == "true-false"){
                                correct_answer_true_false = questions_global[i].choices[j].choice;
                            }
                        }
                    }
                }
            } 
        }

        console.log(correct_answer);

        if(choices.length == correct_answer.length && question_type != "matrix" && question_type != "true-false"){
            for (var i = correct_answer.length - 1; i >= 0; i--) {
                flag = false;
                for(var j = choices.length - 1; j >= 0; j--){
                    if(choices[j] == correct_answer[i]){
                        flag = true;
                        break;
                    }
                }
                if(flag == false){
                    isresponse_true = false;
                    break;
                }
            }
        } 

        else if(question_type == "true-false"){
            if(correct_answer_true_false != choices[0]){
                isresponse_true = false;
            }
            correct_answer = [];
            correct_answer.push(parseInt(correct_answer_true_false,10));
            inp_choice.push(parseInt(choices[0], 10));
        }

        else if(question_type == "matrix"){
            /**var cr_ans = correct_answer_value_matrix.split(",");
            if(cr_ans.length != choices.length){
                isresponse_true = false;
                break;
            }
            //cr_ans.sort();
            //choices.sort();
            console.log(choices, rec_ans);
            for (var i = choices.length - 1; i >= 0; i--) {
                if(cr_ans[i] != rec_ans[i]){
                    isresponse_true = false;
                    break;
                }
            }**/
            var cr_ans = []
            cr_ans = correct_answer_value_matrix.split(",");
            console.log("cr_ans", cr_ans, choices);
            var flag = 1;
            if(cr_ans.length == choices.length){
                for (var i = cr_ans.length - 1; i >= 0; i--) {
                    if(parseInt(cr_ans[i])!=choices[i]){
                        flag = 0;
                        break;
                    }
                }
                correct_answer = cr_ans;
            }
            else{
                isresponse_true = false;
                correct_answer = cr_ans;
            }

            if(flag == 1){
                isresponse_true = true;
                correct_answer = cr_ans;
            } else{
                isresponse_true = false;
                correct_answer = cr_ans;
            }
            
            
            /**if(choices[0] != correct_answer_value_matrix){
                isresponse_true = false;
            }**/
            //correct_answer = [];
            //correct_answer.push(correct_answer_value_matrix);
        }
        else{
            isresponse_true = false;
        }
        console.log('correct_answer', correct_answer, isresponse_true);
        if (question_type == "true-false")
            response = {"is_correctly_answered" : isresponse_true , "right_answers" :correct_answer, "selected_choice": inp_choice};
        else 
            response =  {"is_correctly_answered" : isresponse_true , "right_answers" :correct_answer};
        onAnswerSubmitted(questionId,  window.btoa(JSON.stringify(response)));
}

function onAnswerSubmitted(questionId, responseJSONEncoded) {
    var x = document.getElementsByTagName("HTML");
    console.log('onAnswerSubmitted questionId : ', questionId, x);
    
    var $question = $("#q-" + questionId);
    if (!$question.length)
        return;

    $question.data("attempted", true);
    $question.find(SUBMIT_BTN_SELECTOR).hide();

    var style = $question.data("style") || "";
    var $solution = $question.find(SOLUTION_SELECTOR);
    var $askDoubtBtn = $(".js-ask-doubt-"+questionId);
    var response = decodedBase64ToJSONObject(responseJSONEncoded);
    var rightAnswers = response.right_answers || [];

    console.log('onAnswerSubmitted response : ', response);
    console.log('Right Answer', rightAnswers);
    console.log('style', style);

    if (style == QSTYLE_ASSERTION || style == QSTYLE_SINGLE_CORRECT || style == QSTYLE_MULTIPLE_CORRECT || style == QSTYLE_TRUE_FALSE) {
        displayQuestionAnswer_optionList($question, rightAnswers);
    }

    if (style == QSTYLE_MATRIX) {
        displayQuestionAnswer_matrix($question, rightAnswers);
    }

    if (style == QSTYLE_BLANK) {
        displayQuestionAnswer_blank($question, response.is_correctly_answered, response.right_answer || "");
    }

    $solution.find(".js-stats").remove(",");
    $solution.show();    
    //$askDoubtBtn.show();
}

function getStatsHTML(questionInfoJSONEncoded, is_out_of_syllabus) {
    var html = '';
    try {
        var questionInfo = decodedBase64ToJSONObject(questionInfoJSONEncoded);
        questionInfo.is_out_of_syllabus = is_out_of_syllabus === 'true' ? true : false;
        console.log('onAnswerSubmitted questionInfo', questionInfo);

        html = renderTemplate('question_stats', questionInfo);
        
    } catch(e) {
        console.error('Failed to parse question info JSON');
    }
    return html;
}

function displayQuestionAnswer_optionList($question, rightAnswers) {
    console.log('displayQuestionAnswer_optionList', rightAnswers);
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
            
            $choice.find(CHOICE_RESULT_SELECTOR).html("<div class='text'>Your answer</div>");
        } else if (isRight) {
            $choice.addClass("correct");
            $choice.find(CHOICE_RESULT_SELECTOR).html("<div class='text'>You missed</div>");
        }
    });
}

function displayQuestionAnswer_matrix($question, rightAnswers) {
    var $selectedChoices = $question.find(MATRIX_CHOICE_SELECTED_ITEM_SELECTOR);
    $selectedChoices.addClass("wrong");

    $.each(rightAnswers, function(index, value) {
        var $choice = $question.find(".js-matrix-choice-" + value);
        $choice.removeClass("wrong").addClass("correct");
    });
}

function displayQuestionAnswer_blank($question, isCorrectlyAnswered, rightAnswer) {
    var $input_wrapper = $question.find(".js-input-wrapper");
    var $input = $question.find(BLANK_INPUT_SELECTOR);

    $input.prop("disabled", true);
    $input.attr("placeholder", "");

    if (isCorrectlyAnswered) {
        $input_wrapper.addClass("wrong");
        $input_wrapper.find(".choice-result").html("Wrong answer");
    } else {
        $input_wrapper.addClass("correct");
        $input_wrapper.find(".choice-result").html("Correct answer");
    }
}

function onBookmarkClicked(e) {
    var $bookmarkBtn = $(e.target).closest('.js-bookmark');
    var questionId = $bookmarkBtn.data("id") || 0;
    var isBookmarked = $bookmarkBtn.data("is-bookmarked") || 0;
    var inProgress = $bookmarkBtn.data("in-progress") || 0;

    if ($bookmarkBtn.hasClass("disabled")) {
        var reason = $bookmarkBtn.data("disabled-reason") || '';
        if ("jsObject" in window && reason) {
            jsObject.showToast(reason);
        }
        return;
    }

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

function initPassage($questionWrapper) {
    console.log("begin initPassage");
    // var $passagesText = $(document).find(".js-passage-text");
    // if ($passagesText.length) {
    //     $passagesText.each(function(index){
            
    //     });
    // }

    var $passageText = $questionWrapper.find('.js-passage-text');
    var $passageTextLineNumWrapper = $passageText.find(".line-number-wrapper");
    var passageMarkerLines = {};
    var passageHeight = $passageText.height();
    var lineHeight = parseInt($passageText.css('line-height'), 10);
    var totalLines = passageHeight / lineHeight;

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
        // console.log(markerID, topOffset, lineNum);
    });
    console.log('passageMarkerLines', passageMarkerLines);

    var $questions = $questionWrapper.find(".js-question");
    $questions.each(function(index){
        var $question = $(this);
        var $refs = $question.find('.x-ref');
        $refs.each(function(){
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

    $passageText.addClass('collapsed');
}

function displayQuestions(questionsJSONEncoded) {
    try {
        var questions = decodedBase64ToJSONObject(questionsJSONEncoded);
        questions_global = questions;
        console.log('displayQuestions questions : ', questions);

        if (!questions.length) {
            $contentWrapper.html(renderTemplate('zero_case', {}));
            return;
        } else {
            $contentWrapper.html('');
        }

        $.each(questions, function(index, question) {
            var questionId  = question.question_id || question.id;
            question.testType = 'questionBank';
            question.totalPassageQuesCount = question.passage_child_questions ? question.passage_child_questions.length : 0;
            $contentWrapper.append(renderTemplate('question_wrapper', question));
            var $questionWrapper = $contentWrapper.find('#question-wrapper-'+questionId);

            if (question.question_style === 'passage') {
                var childQuestions = '';
                if (question.passage_child_questions) {
                    question.passage_child_questions.map(function(childQuestion, index){
                        childQuestion.sequenceNo = index + 1;
                        childQuestion.totalQuestion = question.totalPassageQuesCount > 9 ? question.totalPassageQuesCount : '0'+question.totalPassageQuesCount;
                        childQuestion.parentQuestionStyle = 'passage';
                        childQuestion.testType = 'questionBank';
                        childQuestions += renderTemplate('question_unit', childQuestion);
                    });
                }
                
                if (question.passage) {
                    $questionWrapper.find('.js-passage-container').html(renderTemplate('passage', question));
                }
                
                $questionWrapper.find(QUESTION_UNIT_CONTAINER_SELECTOR).html(childQuestions);

                if (question.passage) {
                    initPassage($questionWrapper);
                }
            } else {
                $questionWrapper.find(QUESTION_UNIT_CONTAINER_SELECTOR).html(renderTemplate('question_unit', question));
            }
        });

        setTimeout(function(){
            MathJax.Hub.Queue(['Typeset',MathJax.Hub]);
        },0);
    } catch(e) {
        console.error('Failed to parse question JSON', e);
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

function displayMockQuestions() {
    // var questions =  [
    //     {
    //         "passage_image": "",
    //         "question_image": "",
    //         "hint_available": true,
    //         "passage_footer": "",
    //         "question_linked_to_id": null,
    //         "sequence_no": 1,
    //         "assertion": "",
    //         "hint": "Temperature is one of the basic physical quantities.",
    //         "can_ask_doubt": true,
    //         "question": "Temperature can be expressed as derived quantity in terms of",
    //         "is_bookmarked": false,
    //         "passage": "",
    //         "solution_rating": 0,
    //         "solution_links": [],
    //         "already_attempted": false,
    //         "solution_id": 203750,
    //         "correctly_answered": false,
    //         "passage_header": "",
    //         "solution_image": "",
    //         "reason": "",
    //         "hint_image": "",
    //         "multiple_correct": true,
    //         "question_style": "multiple correct",
    //         //"question_style": "true-false",
    //         "level": 2,
    //         "solution_available": true,
    //         "solution": "<span>Temperature is one of the basic (fundamental) physical quantities.</span>",
    //         "choices": [
    //             {
    //                 "is_right": false,
    //                 "id": 612181,
    //                 "choice": "length and mass",
    //                 "image": "",
    //                 "label": "A"
    //             },
    //             {
    //                 "is_right": false,
    //                 "id": 612182,
    //                 "choice": "mass and time",
    //                 "image": "",
    //                 "label": "B"
    //             },
    //             {
    //                 "is_right": false,
    //                 "id": 612183,
    //                 "choice": "length, mass and time",
    //                 "image": "",
    //                 "label": "C"
    //             },
    //             {
    //                 "is_right": true,
    //                 "id": 612184,
    //                 "choice": "none of these",
    //                 "image": "",
    //                 "label": "D"
    //             }
    //         ],
    //         "question_linked": false,
    //         "id": 178353
    //     },
    //     {
    //         "passage_image": "",
    //         "question_image": "",
    //         "hint_available": false,
    //         "passage_footer": "",
    //         "question_linked_to_id": null,
    //         "sequence_no": 1,
    //         "assertion": "",
    //         "hint": "",
    //         "can_ask_doubt": true,
    //         "question": "Match the following two columns",
    //         "is_bookmarked": false,
    //         "passage": "",
    //         "solution_rating": 0,
    //         "solution_links": [],
    //         "already_attempted": false,
    //         "solution_id": 224813,
    //         "correctly_answered": false,
    //         "passage_header": "",
    //         "solution_image": "",
    //         "reason": "",
    //         "mx_l1_images": [
    //             "",
    //             "",
    //             "",
    //             ""
    //         ],
    //         "hint_image": "",
    //         "mx_l2_images": [
    //             "",
    //             "",
    //             "",
    //             ""
    //         ],
    //         "multiple_correct": false,
    //         "question_style": "matrix",
    //         "level": 4,
    //         "mx_l2": [
    //             "$$[MLT^{-3} A^{-1}]$$",
    //             "$$[ML^3T^{-3} A^{-1}]$$",
    //             "$$[ML^2T^{-3} A^{-1}]$$",
    //             "None of these"
    //         ],
    //         "mx_l1": [
    //             "Electric potential",
    //             "Electric field",
    //             "Electric flux",
    //             "Permitlivity of free space"
    //         ],
    //         "solution_available": true,
    //         "solution": "Mass $$=[M], $$ length $$= [L], $$&#160; time $$=T$$, current $$=[A]$$<br/>$$F=qE$$&#160;  where $$E=$$ electric field.<br/>$$[MLT^{-2}]=[AT][E]$$ as $$I=\\dfrac{dq}{dt}$$<br/>$$[E]=[MLA^{-1}T^{-3}]$$&#160; thus $$B \\rightarrow 1$$<br/><br/><div>As $$E=-\\dfrac{dV}{dx}$$ so $$[V]=[E][L]=[ML^2A^{-1}T^{-3}]$$ thus $$A \\rightarrow 3$$<br/><br/></div><div>Electric flux $$(\\phi)= $$ electric filed $$\\times $$ area.<br/>$$\\therefore [\\phi]=[E][L^2]=[ML^3A^{-1}T^{-3}]$$&#160; thus $$C \\rightarrow 2$$<br/><br/></div><div>As $$E=\\dfrac{q}{4\\pi \\epsilon_0}$$ so $$[\\epsilon_0]=\\dfrac{[q]}{[E]}=\\dfrac{[AT]}{[MLA^{-1}T^{-3}]}=[M^{-1}L^{-1}A^{2}T^{4}]$$ thus $$D \\rightarrow 4$$<br/></div>",
    //         "choices": [
    //             {
    //                 "is_right": true,
    //                 "id": 721419,
    //                 "choice": "2,4,9,15",
    //                 "image": "",
    //                 "label": "A"
    //             }
    //         ],
    //         "question_linked": false,
    //         "id": 223006
    //     },
    //     {
    //         "passage_image": "",
    //         "question_image": "",
    //         "hint_available": true,
    //         "passage_footer": "",
    //         "question_linked_to_id": null,
    //         "sequence_no": 3,
    //         "assertion": "",
    //         "hint": "The meter is a unit and not a physical quantity.",
    //         "can_ask_doubt": true,
    //         "question": "Which of the following is not the name of a physical quantity?",
    //         "is_bookmarked": false,
    //         "passage": "",
    //         "solution_rating": 0,
    //         "solution_links": [],
    //         "already_attempted": false,
    //         "solution_id": 203749,
    //         "correctly_answered": false,
    //         "passage_header": "",
    //         "solution_image": "",
    //         "reason": "",
    //         "hint_image": "",
    //         "multiple_correct": false,
    //         "question_style": "single correct",
    //         "level": 2,
    //         "solution_available": true,
    //         "solution": "Meter is a unit of length&#160;and not a physical quantity while rest are&#160;physical quantities.",
    //         "choices": [
    //             {
    //                 "is_right": false,
    //                 "id": 612154,
    //                 "choice": "Displacement",
    //                 "image": "",
    //                 "label": "A"
    //             },
    //             {
    //                 "is_right": false,
    //                 "id": 612155,
    //                 "choice": "Momentum",
    //                 "image": "",
    //                 "label": "B"
    //             },
    //             {
    //                 "is_right": true,
    //                 "id": 612156,
    //                 "choice": "Meter",
    //                 "image": "",
    //                 "label": "C"
    //             },
    //             {
    //                 "is_right": false,
    //                 "id": 612157,
    //                 "choice": "Torque",
    //                 "image": "",
    //                 "label": "D"
    //             }
    //         ],
    //         "question_linked": false,
    //         "id": 178344
    //     },
    // ];

    var questions = [{"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "Pressure is determined as force per unit area of the surface. The SI unit of pressure, pascal is as shown below:&#160;<br/>$$\\displaystyle 1\\;Pa=1\\;N\\;{ m }^{ -2 }$$<br/>If mass of air at sea level is $$\\displaystyle 1034 \\ g\\;{cm }^{ -2 }$$,&#160;&#160;calculate the pressure in pascal.<br/>", "id": 422312, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 21, "solution": "Pressure $$\\displaystyle = $$ weight per unit area $$\\displaystyle = mg = 1034g/cm^2 \\times \\frac {1kg}{1000g} \\times (\\frac {100\\;cm}{1m})^2 \\times 9.80 m/s^2 \\times \\frac {1N}{kgm/s^2} \\times \\frac {1Pa}{1N/m^2}$$<div>$$= 1.013 \\times 10^5 Pa$$<br/></div>", "solution_id": 390266, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "What is the SI unit of mass? How is it defined?", "id": 422315, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 22, "solution": "Kilogram is used as SI unit of mass. It is represented by the symbol kg.&#160;<div>It is defined as the mass of platinium-iridium block stored at international bureau of weights and measures in France.<br/></div>", "solution_id": 390263, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "Match the following prefixes with their multiples:<br/>", "id": 422323, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "matrix", "solution_available": true, "reason": "", "sequence_no": 23, "solution": "<table class=\"table table-bordered\"><tbody><tr><td>&#160;</td><td>&#160;Prefixes</td><td>Multiples <br/></td></tr><tr><td>&#160;1</td><td>&#160;micro</td><td>$$\\displaystyle 10^{-6} $$<br/></td></tr><tr><td>&#160;2</td><td>&#160;deca</td><td>$$\\displaystyle 10^{} $$<br/></td></tr><tr><td>&#160;3</td><td>&#160;mega</td><td>$$\\displaystyle 10^{6} $$<br/></td></tr><tr><td>&#160;4</td><td>&#160;giga</td><td>$$\\displaystyle 10^{9} $$<br/></td></tr><tr><td>&#160;5</td><td>&#160;femto</td><td>$$\\displaystyle 10^{-15} $$<br/></td></tr></tbody></table>", "solution_id": 390267, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [{"is_right": "true", "id": 1799350, "image": "", "label": "A", "choice": "2,9,10,16,23"}], "mx_l1_images": ["", "", "", "", ""], "mx_l2_images": ["", "", "", "", ""], "mx_l1": ["<span>micro</span>", "deca", "mega", "giga", "femto"], "mx_l2": ["$$10^6$$", "$$10^9$$", "$$10^{-6}$$", "$$10^{-15}$$", "$$10$$"]}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "What do you mean by significant figures?", "id": 422326, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 24, "solution": "In a given number, all the certain digits plus one doubtful digit correspond to significant figures. They depend on the precision of the scale or the instrument used for the measurement.&#160; For example, the number $$1.587$$ represents four significant figures (three are certain and one is doubtful).<br/>", "solution_id": 390269, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "A sample of drinking water was found to be severely contaminated with chloroform, $$\\displaystyle { CHCl }_{ 3 }$$,&#160;supposed to be carcinogenic in nature. The level of contamination was 15 ppm (by mass).<br/>(i) Express this in percent by mass.<br/>(ii) Determine the molality of chloroform in the water sample.<br/>", "id": 422329, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 25, "solution": "(1) 15 parts of chlorofom in 1,000,000 parts of water corresponds to 15 ppm.<br/>Percentage by mass of chloroform $$\\displaystyle = \\frac {15 \\times 100}{1,000,000} = 1.5 \\times 10^{-3} $$ %<br/>(2) The molar mass of chloroform is $$\\displaystyle 12+1+3+\\ \\times 35.5 = 119.5 $$ g/mol.<br/>The number of moles of chloroform are $$\\displaystyle \\frac {1.5 \\times 10^{-3}g}{119.5g/mol}=1.255 \\times 10^{-5} $$ mol.<br/>The mass of water is 100 g.<br/>Molality is the number of moles of solute in 1,000 g of solvent.<br/>Hence, the molality of the solution is $$\\displaystyle \\frac {1.255 \\times 10^{-5}}{100} \\times 1000 = 1.255 \\times 10^{-4} m $$.<br/>", "solution_id": 390272, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "Express the following in the scientific notation:<br>(i) 0.0048 <br>(ii) 234,000 <br>(iii) 8008 <br>(iv) 500.0 <br>(v) 6.0012<br>", "id": 422337, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 26, "solution": "(i) 0.0048 can be written in scientific notation as $$\\displaystyle 4.8\\times { 10 }^{ -3 }$$.<br/>(ii) 234,000 can be written in scientific notation as&#160; $$\\displaystyle 2.34\\times { 10 }^{ 5 }$$.<br/>(iii) 8008 can be written in scientific notation as $$\\displaystyle 8.008\\times { 10 }^{ 3 }$$.<br/>(iv) 500.0 can be written in scientific notation as $$\\displaystyle 5.000\\times { 10 }^{ 2 }$$.<br/>(v) 6.0012 can be written in scientific notation as $$\\displaystyle 6.0012$$.", "solution_id": 390276, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "How many significant figures are present in the following?<br>(i) 0.0025 <br>(ii) 208 <br>(iii) 5005 <br>(iv) 126,00<br>(v) 500.0<br>(vi)&nbsp;2.0034&nbsp;", "id": 422341, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 27, "solution": "(i) 0.0025 contains 2 significant figures.<br/>(ii) 208 contains 3 significant figures.<br/>(iii) 5005 contains 4 significant figures.<br/>(iv) 126,00 contains 3 significant figures.<br/>(v) 500.0 contains 4 significant figures.<br/>(vi) 2.0034 contains 5 significant figures.", "solution_id": 390277, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "Round up the following upto three significant figures:<br>(i) 34.216<br>(ii) 10.4107&nbsp;<br>(iii) 0.04597&nbsp;<br>(iv) 2808&nbsp;<br>", "id": 422346, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 28, "solution": "(i) The number 34.216 is rounded to three significant figures as 34.2.<br/>(ii) The number 10.4107&#160; is rounded to three significant figures as 10.4.<br/>(iii) The number 0.04597&#160; is rounded to three significant figures as 0.046.<br/>(iv) The number 2808&#160; is rounded to three significant figures as 2810.", "solution_id": 390278, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "The following data are obtained when dinitrogen and dioxygen react together to form different compounds :<br/><table class=\"wysiwyg-table\"><tbody><tr><td></td><td>Mass of dinitrogen</td><td>Mass of dioxygen</td></tr><tr><td>(i)</td><td>14g</td><td>16g</td></tr><tr><td>(ii)</td><td>14g</td><td>32g</td></tr><tr><td>(iii)</td><td>28g</td><td>32g</td></tr><tr><td>(iv)</td><td>28g</td><td>80g</td></tr></tbody></table>(a)&#160;Which law of chemical combination is obeyed by the above experimental data? Give its statement.<div><br/>(b)&#160;Fill in the blanks in the following conversions:<br/>(i) 1 km = <u>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; </u>mm = <u>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; </u> pm <br/>(ii) 1 mg = <u>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; </u>kg = <u>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; </u> ng <br/>(iii) 1 mL = <u>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; </u> L = <u>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; </u>$$\\displaystyle { dm }^{ 3 }$$&#160;<br/><br/></div>", "id": 422367, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 29, "solution": "(a) When the mass of dinitrogen is 28g , mass of dioxygen combined is 32,64,32, and 80 g . The corresponding ratio is 1:2:1:5. It is a simple whole number ratio .<br/>This illustrates the law of multiple proportions.<br/>It states that, &#34;if two elements can combine to form more than one compound, the masses of one element that combine with a fixed mass of the other element, are in the ratio of small whole numbers.<div>(b) (i) $$1\\;km = 1000\\;m\\times 100\\;cm\\times 10\\;mm$$</div><div>$$\\therefore 1\\;km = 10^6 \\;mm$$</div><div>$$1\\;km = 1000\\;m\\times \\dfrac{1\\;pm}{10^{-12}m}$$</div><div><span>$$\\therefore 1\\;km = 10^15 \\;pm$$</span><br/></div><div><span>Hence, $$1\\;km = 10^6\\;mm = 10^{15}\\;pm$$</span></div><div><span>(ii) &#160;$$1\\;mg = 10^{-6}\\;kg=10^{6}\\;ng$$</span></div><div><span>(iii) $$1\\;ml = 10^{-3}\\;l = 10^{-3}\\;dm^3$$</span></div>", "solution_id": 402202, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}, {"n_pages": 4, "already_attempted": false, "assertion": "", "can_ask_doubt": false, "correctly_answered": false, "hint": "", "hint_available": false, "disable_bookmark": true, "hint_image": "", "is_bookmarked": false, "level": 2, "multiple_correct": false, "passage": "", "passage_footer": "", "passage_header": "", "passage_image": "", "question": "If the speed of light is $$\\displaystyle 3.0\\times { 10 }^{ 8 }m{ s }^{ -1 }$$,&nbsp;calculate the distance covered by light in 2.00 ns.", "id": 422370, "question_linked": false, "question_linked_to_id": null, "question_status": "published", "question_style": "subjective", "solution_available": true, "reason": "", "sequence_no": 30, "solution": "The distance traveled is the product of speed and time.<br/>Thus, in 2.00 ns, the light will travel a distance of $$\\displaystyle \\frac {3.00 \\times 10^8 m/s \\times 2.00ns \\times 10^{-9}s}{1ns}= 0.600m $$<br/>", "solution_id": 390279, "solution_image": "", "solution_rating": 0, "solution_links": [], "choices": [], "mx_l1_images": [], "mx_l2_images": [], "mx_l1": [], "mx_l2": []}];

    displayQuestions(window.btoa(JSON.stringify(questions)));
}
