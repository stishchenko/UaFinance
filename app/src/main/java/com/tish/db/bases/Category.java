package com.tish.db.bases;

import com.tish.R;

public enum Category {
    FOOD(R.string.category_food, R.drawable.category_food_icon, R.color.food),
    HEALTH(R.string.category_health, R.drawable.category_health_icon, R.color.health),
    HOME(R.string.category_home, R.drawable.category_home_icon, R.color.home),
    HYGIENE(R.string.category_hygiene, R.drawable.category_hygiene_icon, R.color.hygiene),
    CLOTHES(R.string.category_clothes, R.drawable.category_clothes_icon, R.color.clothes),
    HOUSECOSTS(R.string.category_housecosts, R.drawable.category_housecosts_icon, R.color.housecosts),
    TRANSPORT(R.string.category_transport, R.drawable.category_transport_icon, R.color.transport),
    CAR(R.string.category_car, R.drawable.category_car_icon, R.color.car),
    CAFFEE(R.string.category_caffee, R.drawable.category_caffee_icon, R.color.caffee),
    CINEMA(R.string.category_cinema, R.drawable.category_cinema_icon, R.color.cinema),
    TRAVEL(R.string.category_travel, R.drawable.category_travel_icon, R.color.travel),
    EDUCATION(R.string.category_education, R.drawable.category_education_icon, R.color.education),
    HOBBY(R.string.category_hobby, R.drawable.category_hobby_icon, R.color.hobby),
    GIFTS(R.string.category_gifts, R.drawable.category_gifts_icon, R.color.gifts),
    CHILDREN(R.string.category_children, R.drawable.category_children_icon, R.color.children),
    PETS(R.string.category_pets, R.drawable.category_pets_icon, R.color.pets),
    MAKEUP(R.string.category_makeup, R.drawable.category_makeup_icon, R.color.makeup),
    OTHER(R.string.category_other, R.drawable.category_other_icon, R.color.other);

    private int categoryName;
    private int iconResource;
    private int colorResource;

    Category(int categoryName, int iconResource, int colorResource) {
        this.categoryName = categoryName;
        this.iconResource = iconResource;
        this.colorResource = colorResource;
    }

    public int getCategoryName() {
        return categoryName;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int getColorResource() {
        return colorResource;
    }
}
