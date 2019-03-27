package com.example.user.skillbarter;


public class Skill {
    private String skillID;
    private String category;
    private String skillName;

    private void updateSkillId(){
        this.skillID = this.category + "." + this.skillName;
    }

    public Skill(String category, String skillName) {
        this.category = category;
        this.skillName = skillName;
        updateSkillId();
    }

    public String getSkillID() {
        return skillID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        updateSkillId();
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
        updateSkillId();
    }
}
