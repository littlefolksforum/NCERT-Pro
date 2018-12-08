package com.urexamhelp.ncertpro;

public class Chapter {
        private String chapter_id;
        private String chapter_name;
        private int question_count;



        public Chapter(String chapter_id ,String chapter_name, int question_count){
            this.chapter_id=chapter_id;
            this.chapter_name=chapter_name;
            this.question_count= question_count;
        }
        public String getChapter_id(){return chapter_id; }
        public String getChapter_name(){ return chapter_name; }
        public int getQuestion_count(){return question_count;}

}
