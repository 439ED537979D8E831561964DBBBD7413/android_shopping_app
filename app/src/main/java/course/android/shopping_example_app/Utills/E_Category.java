package course.android.shopping_example_app.Utills;


public enum E_Category {

    VEHICLE("VEHICLE"),
    TECH("TECH"),
    SPORT("SPORT"),
    HOME("HOME"),
    CLOTHES("CLOTHES"),
    OTHER("OTHER");

    private String type;

    E_Category(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
