package com.healthfuel.myhealthfuel.mainpage;

public class Foods {

    String product_name, image_url, id;
    Integer kcal, quantity;
    Double carbohydrates, fat, proteins, fiber, sugar, salt;

    private String foodId;
    public Foods() {

    }

    public Foods(String product_name, String image_url, String id, Double carbohydrates, Integer kcal, Double fat, Double proteins, Double fiber, Double sugar, Double salt, Integer quantity) {
        this.product_name = product_name;
        this.image_url = image_url;
        this.id = id;
        this.carbohydrates = carbohydrates;
        this.kcal = kcal;
        this.fat = fat;
        this.proteins = proteins;
        this.fiber = fiber;
        this.sugar = sugar;
        this.salt = salt;
        this.quantity = quantity;
    }




    public String getProduct_name() {
        return product_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getId() {
        return id;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public Integer getKcal() {
        return kcal;
    }

    public Double getFat() {
        return fat;
    }

    public Double getProteins() {
        return proteins;
    }

    public Double getFiber() {
        return fiber;
    }

    public Double getSugar() {
        return sugar;
    }

    public Double getSalt() {
        return salt;
    }
    public Integer getQuantity() {
        return quantity;
    }


    public String getFoodId() {
        return foodId;
    }
}
