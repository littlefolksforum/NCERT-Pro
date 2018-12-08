package com.urexamhelp.ncertpro;

public class Subject {
private String subject_id;
private String subject_name;
private int chapter_count;



public Subject(String subject_id ,String subject_name, int chapter_count){
    this.subject_id=subject_id;
    this.subject_name=subject_name;
    this.chapter_count= chapter_count;
}
public String getSubject_id(){
    return subject_id;
}
public String getSubject_name(){ return subject_name; }
public int getChapter_count(){return chapter_count;}

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public void setChapter_count(int chapter_count) {
        this.chapter_count = chapter_count;
    }
}
