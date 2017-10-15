package com.example.bipullohia.fauxifyrestaurant;

class CurrentOrder{

    private String currentdishName, currentdishPrice; private Integer currentdishQuantity;

        CurrentOrder(String currentdishName, String currentdishPrice, Integer currentdishQuantity) {
            this.currentdishName = currentdishName;
            this.currentdishPrice = currentdishPrice;
            this.currentdishQuantity = currentdishQuantity;
        }

        String getCurrentdishName() {
            return currentdishName;
        }

        public void setCurrentdishName(String name) { this.currentdishName = name; }

        String getCurrentdishPrice() {
            return currentdishPrice;
        }

        public void setCurrentdishPrice(String name) {
            this.currentdishPrice = name;
        }

        Integer getCurrentdishQuantity() {
            return currentdishQuantity;
        }

        public void setCurrentdishQuantity(Integer name) {
            this.currentdishQuantity = name;
        }
}
